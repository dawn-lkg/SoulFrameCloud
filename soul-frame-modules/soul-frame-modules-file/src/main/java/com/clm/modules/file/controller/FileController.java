package com.clm.modules.file.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.annotation.Log;
import com.clm.modules.system.domain.dto.FileUploadDTO;
import com.clm.modules.system.domain.param.FileQueryParam;
import com.clm.modules.system.domain.vo.FileVO;
import com.clm.modules.system.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件控制器
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
@Slf4j
@Tag(name = "文件管理")
@RestController
@RequestMapping("/system/file")
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final FileService fileService;


    @Operation(summary = "分页查询文件列表")
    @GetMapping("/page")
    @Log(businessType = BusinessType.QUERY)
    public Result<IPage<FileVO>> page(FileQueryParam  param) {
        return success(fileService.selectFilePage(param));
    }

    @Operation(summary = "查询所有文件列表")
    @GetMapping
    @Log(businessType = BusinessType.QUERY)
    public Result<List<FileVO>> list(FileQueryParam param) {
        return success(fileService.selectFileList(param));
    }

    @Operation(summary = "获取文件详情")
    @GetMapping("/{fileId}")
    @Log(businessType = BusinessType.QUERY)
    public Result<FileVO> getInfo(@PathVariable("fileId") Long fileId) {
        return success(fileService.getFileInfo(fileId));
    }

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Log(businessType = BusinessType.INSERT)
    public Result<FileVO> upload(@RequestPart("file") MultipartFile file,
                                 @RequestParam(value = "storageType", required = false) String storageType,
                                 @RequestParam(value = "isPublic", required = false) Boolean isPublic,
                                 @RequestParam(value = "isEncrypted", required = false) Boolean isEncrypted,
                                 @RequestParam(value = "description", required = false) String description) {
        FileUploadDTO uploadDTO = new FileUploadDTO();
        uploadDTO.setFile(file);
        uploadDTO.setStorageType(storageType);
        uploadDTO.setIsPublic(isPublic);
        uploadDTO.setIsEncrypted(isEncrypted);
        uploadDTO.setDescription(description);
        return success(fileService.uploadFile(uploadDTO));
    }

    @Operation(summary = "批量上传文件")
    @PostMapping(value = "/batch-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Log(businessType = BusinessType.INSERT)
    public Result<List<FileVO>> batchUpload(@RequestPart("files") List<MultipartFile> files) {
        return success(fileService.batchUploadFiles(files));
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download/{fileId}")
    @Log(businessType = BusinessType.EXPORT)
    public void download(@PathVariable("fileId") Long fileId, HttpServletResponse response) throws IOException {
        try (InputStream inputStream = fileService.downloadFile(fileId)) {
            // 获取文件信息
            FileVO fileInfo = fileService.getFileInfo(fileId);
            
            // 设置响应头
            response.setContentType(fileInfo.getMimeType());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            
            // 设置文件名
            String encodedFileName = URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            
            // 写入响应
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            response.flushBuffer();
        } catch (Exception e) {
            log.error("文件下载失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("文件下载失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取文件URL")
    @GetMapping("/url/{fileId}")
    public Result<String> getFileUrl(@PathVariable("fileId") Long fileId) {
        return success(fileService.getFileUrl(fileId));
    }



    @Operation(summary = "更新文件信息")
    @PutMapping("/{fileId}")
    @Log(businessType = BusinessType.UPDATE)
    public Result<FileVO> update(@PathVariable("fileId") Long fileId,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) Boolean isPublic) {
        return success(fileService.updateFileInfo(fileId, description, isPublic));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{fileId}")
    @Log(businessType = BusinessType.DELETE)
    public Result<Boolean> delete(@PathVariable("fileId") Long fileId) {
        return success(fileService.deleteFile(fileId));
    }

    @Operation(summary = "批量删除文件")
    @DeleteMapping("/batch")
    @Log(businessType = BusinessType.DELETE)
    public Result<Boolean> batchDelete(@RequestBody @Validated List<Long> fileIds) {
        return success(fileService.batchDeleteFiles(fileIds));
    }
} 