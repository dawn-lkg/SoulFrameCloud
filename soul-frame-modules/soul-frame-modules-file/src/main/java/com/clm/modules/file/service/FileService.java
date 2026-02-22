package com.clm.modules.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.file.domain.dto.FileUploadDTO;
import com.clm.modules.file.domain.entity.File;
import com.clm.modules.file.domain.param.FileQueryParam;
import com.clm.modules.file.domain.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件服务接口
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
public interface FileService extends IService<File> {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件url
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传文件
     *
     * @param uploadDTO 上传文件信息
     * @return 文件信息
     */
    FileVO uploadFile(FileUploadDTO uploadDTO);

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件信息列表
     */
    List<FileVO> batchUploadFiles(List<MultipartFile> files);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件输入流
     */
    InputStream downloadFile(Long fileId);

    /**
     * 获取文件访问URL
     *
     * @param fileId 文件ID
     * @return 文件URL
     */
    String getFileUrl(Long fileId);

    /**
     * 分页查询文件列表
     * @param param 分页查询参数
     * @return 分页结果
     */
    IPage<FileVO> selectFilePage(FileQueryParam param);

    /**
     * 查询文件列表
     * @param param 查询参数
     * @return 文件列表
     */
    List<FileVO> selectFileList(FileQueryParam param);

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @return 文件详情
     */
    FileVO getFileInfo(Long fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @return 是否成功
     */
    boolean batchDeleteFiles(List<Long> fileIds);

    /**
     * 更新文件信息
     *
     * @param fileId 文件ID
     * @param description 文件描述
     * @param isPublic 是否公开
     * @return 更新后的文件信息
     */
    FileVO updateFileInfo(Long fileId, String description, Boolean isPublic);
} 