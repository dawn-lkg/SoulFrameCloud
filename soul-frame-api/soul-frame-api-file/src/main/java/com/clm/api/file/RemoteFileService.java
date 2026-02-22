package com.clm.api.file;

import com.clm.api.file.domain.FileUploadDTO;
import com.clm.api.file.domain.FileVO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 远程文件服务
 *
 * @author 陈黎明
 */
@FeignClient(contextId = "RemoteFileService", value = ServiceNameConstants.FILE_SERVICE)
public interface RemoteFileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileVO> upload(@RequestPart("file") MultipartFile file);

    @PostMapping(value = "/file/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> uploadFile(@RequestPart("file") MultipartFile file);

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件信息列表
     */
    @PostMapping(value = "/file/batch-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<List<FileVO>> batchUpload(@RequestPart("files") List<MultipartFile> files);

    /**
     * 获取文件访问URL
     *
     * @param fileId 文件ID
     * @return 文件URL
     */
    @GetMapping("/file/url/{fileId}")
    Result<String> getFileUrl(@PathVariable("fileId") Long fileId);

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @return 文件详情
     */
    @GetMapping("/file/{fileId}")
    Result<FileVO> getFileInfo(@PathVariable("fileId") Long fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    @DeleteMapping("/file/{fileId}")
    Result<Boolean> deleteFile(@PathVariable("fileId") Long fileId);

    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @return 是否成功
     */
    @DeleteMapping("/file/batch")
    Result<Boolean> batchDeleteFiles(@RequestBody List<Long> fileIds);
}
