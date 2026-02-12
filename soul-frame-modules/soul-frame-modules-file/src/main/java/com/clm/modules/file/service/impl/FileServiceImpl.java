package com.clm.modules.file.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.common.core.utils.IpUtils;
import com.clm.common.core.utils.ServletUtils;
import com.clm.common.core.utils.UserAgentUtils;
import com.clm.modules.system.domain.dto.FileUploadDTO;
import com.clm.modules.system.domain.entity.File;
import com.clm.modules.system.domain.param.FileQueryParam;
import com.clm.modules.system.domain.vo.FileVO;
import com.clm.modules.system.mapper.FileMapper;
import com.clm.modules.system.service.ConfigService;
import com.clm.modules.system.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文件服务实现类
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucketName}")
    private String defaultBucketName;

    @Value("${minio.url-expiry:7}")
    private Integer urlExpiry;

    @Value("${file.local-path:/uploads}")
    private String localFilePath;

    private final ConfigService configService;

    @Override
    public IPage<FileVO> selectFilePage(FileQueryParam param) {
        Page<FileVO> pageParam = new Page<>(param.getPageNum(), param.getPageSize());
        return baseMapper.selectFilePage(pageParam, param);
    }

    @Override
    public List<FileVO> selectFileList(FileQueryParam param) {
        return baseMapper.selectFileList(param);
    }

    @Override
    public FileVO getFileInfo(Long fileId) {
        File fileEntity = getById(fileId);
        if (fileEntity == null) {
            throw new BaseException(HttpCodeEnum.DATA_NOT_EXIST.getCode(), "文件不存在");
        }

        FileVO fileVO = convertToFileVO(fileEntity);
        fileVO.setFileUrl(getFileUrl(fileId));
        return fileVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId) {
        File fileEntity = getById(fileId);
        if (fileEntity == null) {
            return false;
        }

        try {
            // 删除物理文件
            if ("local".equals(fileEntity.getStorageType())) {
                FileUtil.del(fileEntity.getStoragePath());
            } else {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(fileEntity.getBucketName())
                                .object(fileEntity.getStoragePath())
                                .build()
                );
            }

            // 删除数据库记录
            return removeById(fileId);
        } catch (Exception e) {
            log.error("删除文件失败", e);
            throw new BaseException(HttpCodeEnum.ERROR.getCode(), "删除文件失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteFiles(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return false;
        }

        for (Long fileId : fileIds) {
            deleteFile(fileId);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFile(file);
        fileUploadDTO.setStorageType("minio");
        fileUploadDTO.setIsPublic(false);
        fileUploadDTO.setIsEncrypted(false);
        FileVO fileVO = uploadFile(fileUploadDTO);
        if (fileVO == null) {
            return null;
        }
        return fileVO.getFileUrl().substring(0, fileVO.getFileUrl().indexOf("?"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadFile(FileUploadDTO uploadDTO) {
        try {
            MultipartFile file = uploadDTO.getFile();
            if (file == null || file.isEmpty()) {
                throw new BaseException(HttpCodeEnum.BAD_REQUEST.getCode(), "上传文件不能为空");
            }

            String configStorageType = configService.getValueByKey("storage.storageType", String.class);

            String originalFilename = file.getOriginalFilename();
            String fileExtension = FileUtil.extName(originalFilename);
            String mimeType = file.getContentType();
            long fileSize = file.getSize();
            String fileHash = DigestUtil.sha256Hex(file.getInputStream());

            // 生成唯一文件名
            String fileName = IdUtil.fastSimpleUUID() + "." + fileExtension;
            
            // 存储类型，默认为local
            String storageType = StrUtil.isBlank(uploadDTO.getStorageType()) ? configStorageType : uploadDTO.getStorageType();
            
            // 存储路径
            String storagePath;
            String bucketName = defaultBucketName;
            
            // 根据存储类型处理文件
            if ("minio".equals(storageType)) {
                storagePath = uploadToMinio(file, fileName, bucketName);
            } else {
                storagePath = uploadToLocalStorage(file, fileName);
                storageType = "local";
            }


            // 创建文件记录
            File fileEntity = new File();
            fileEntity.setFileName(fileName);
            fileEntity.setFileExtension(fileExtension);
            fileEntity.setOriginalName(originalFilename);
            fileEntity.setFileSize(fileSize);
            fileEntity.setFileHash(fileHash);
            fileEntity.setMimeType(mimeType);
            fileEntity.setStoragePath(storagePath);
            fileEntity.setStorageType(storageType);
            fileEntity.setBucketName(bucketName);
            fileEntity.setOwnerId(StpUtil.getLoginIdAsLong());
            fileEntity.setStatus("active");
            fileEntity.setIsPublic(uploadDTO.getIsPublic() != null ? uploadDTO.getIsPublic() : false);
            fileEntity.setIsEncrypted(uploadDTO.getIsEncrypted() != null ? uploadDTO.getIsEncrypted() : false);
            fileEntity.setVersionNumber(1);
            fileEntity.setIsLatestVersion(true);
            fileEntity.setUploadIp(IpUtils.getIpAddr());
            fileEntity.setUploadUserAgent(UserAgentUtils.getUserAgent());
            fileEntity.setUploadSource("web");
            fileEntity.setDownloadCount(0);
            fileEntity.setViewCount(0);
            fileEntity.setDescription(uploadDTO.getDescription());
            String fileUrl = getFileUrl(fileEntity);
            fileEntity.setFileUrl(fileUrl.substring(0, fileUrl.indexOf("?")));

            if (mimeType != null && mimeType.startsWith("image/")) {
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                fileEntity.setWidth(width);
                fileEntity.setHeight(height);
            }
            
            // 保存到数据库
            if (!save(fileEntity)) {
                throw new BaseException(HttpCodeEnum.ERROR.getCode(), "保存文件记录失败");
            }
            
            // 转换为VO返回
            FileVO fileVO = convertToFileVO(fileEntity);
            fileVO.setFileUrl(getFileUrl(fileEntity.getFileId()));
            
            return fileVO;
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new BaseException(HttpCodeEnum.ERROR.getCode(), "上传文件失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileVO> batchUploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BaseException(HttpCodeEnum.BAD_REQUEST.getCode(), "上传文件不能为空");
        }
        
        return files.stream().map(file -> {
            FileUploadDTO uploadDTO = new FileUploadDTO();
            uploadDTO.setFile(file);
            return uploadFile(uploadDTO);
        }).collect(Collectors.toList());
    }

    @Override
    public InputStream downloadFile(Long fileId) {
        try {
            File fileEntity = getById(fileId);
            if (fileEntity == null) {
                throw new BaseException(HttpCodeEnum.DATA_NOT_EXIST.getCode(), "文件不存在");
            }
            
            // 更新下载次数
            baseMapper.updateAccessCount(fileId, "download");
            
            // 根据存储类型获取文件流
            if ("local".equals(fileEntity.getStorageType())) {
                return FileUtil.getInputStream(fileEntity.getStoragePath());
            } else {
                // MinIO下载
                return minioClient.getObject(
                    GetObjectArgs.builder()
                        .bucket(fileEntity.getBucketName())
                        .object(fileEntity.getStoragePath())
                        .build()
                );
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
            throw new BaseException(HttpCodeEnum.ERROR.getCode(), "下载文件失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(Long fileId) {
        File fileEntity = getById(fileId);
        if (fileEntity == null) {
            throw new BaseException(HttpCodeEnum.DATA_NOT_EXIST.getCode(), "文件不存在");
        }

        // 更新查看次数
        baseMapper.updateAccessCount(fileId, "view");

        try {
            if ("local".equals(fileEntity.getStorageType())) {
                // 本地文件URL
                String serverUrl = ServletUtils.getServerUrl();
                return serverUrl + "/api/file/download/" + fileId;
            } else {
                // MinIO文件URL
                return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(fileEntity.getBucketName())
                        .object(fileEntity.getStoragePath())
                        .expiry(urlExpiry, TimeUnit.DAYS)
                        .build()
                );
            }
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            throw new BaseException(HttpCodeEnum.ERROR.getCode(), "获取文件URL失败: " + e.getMessage());
        }
    }

    public String getFileUrl(File file) {
        try {
            if ("minio".equals(file.getStorageType())) {
                // MinIO文件URL
                return minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(file.getBucketName())
                                .object(file.getStoragePath())
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            throw new BaseException(HttpCodeEnum.ERROR.getCode(), "获取文件URL失败: " + e.getMessage());
        }
        return "";
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO updateFileInfo(Long fileId, String description, Boolean isPublic) {
        File fileEntity = getById(fileId);
        if (fileEntity == null) {
            throw new BaseException(HttpCodeEnum.DATA_NOT_EXIST.getCode(), "文件不存在");
        }
        
        if (description != null) {
            fileEntity.setDescription(description);
        }
        
        if (isPublic != null) {
            fileEntity.setIsPublic(isPublic);
        }
        fileEntity.setUpdateBy(StpUtil.getLoginIdAsString());
        
        updateById(fileEntity);
        
        FileVO fileVO = convertToFileVO(fileEntity);
        fileVO.setFileUrl(getFileUrl(fileId));
        return fileVO;
    }
    
    /**
     * 上传文件到MinIO
     *
     * @param file 文件
     * @param fileName 文件名
     * @param bucketName 存储桶
     * @return 存储路径
     */
    private String uploadToMinio(MultipartFile file, String fileName, String bucketName) throws IOException {
        try {
            // 检查存储桶是否存在，不存在则创建
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            
            // 按日期生成存储路径
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String objectName = datePath + "/" + fileName;
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            return objectName;
        } catch (Exception e) {
            log.error("上传文件到MinIO失败", e);
            throw new IOException("上传文件到MinIO失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上传文件到本地存储
     *
     * @param file 文件
     * @param fileName 文件名
     * @return 存储路径
     */
    private String uploadToLocalStorage(MultipartFile file, String fileName) throws IOException {
        // 按日期生成存储路径
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dirPath = localFilePath + "/" + datePath;
        
        // 创建目录
        FileUtil.mkdir(dirPath);
        
        // 完整文件路径
        String filePath = dirPath + "/" + fileName;
        
        // 保存文件
        file.transferTo(FileUtil.file(filePath));
        
        return filePath;
    }
    
    /**
     * 将实体转换为VO
     *
     * @param fileEntity 文件实体
     * @return 文件VO
     */
    private FileVO convertToFileVO(File fileEntity) {
        FileVO fileVO = new FileVO();
        BeanUtils.copyProperties(fileEntity, fileVO);
        return fileVO;
    }
} 