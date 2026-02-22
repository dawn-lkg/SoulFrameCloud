package com.clm.api.file.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 文件上传DTO
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
@Data
@Schema(description = "文件上传数据")
public class FileUploadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 上传文件
     */
    @Schema(description = "上传文件")
    private MultipartFile file;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 是否公开
     */
    @Schema(description = "是否公开")
    private Boolean isPublic;

    /**
     * 是否加密
     */
    @Schema(description = "是否加密")
    private Boolean isEncrypted;

    /**
     * 文件描述
     */
    @Schema(description = "文件描述")
    private String description;
}
