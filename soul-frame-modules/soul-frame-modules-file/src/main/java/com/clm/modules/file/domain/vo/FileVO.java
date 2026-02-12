package com.clm.modules.file.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息VO
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
@Data
@Schema(description = "文件信息")
public class FileVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识
     */
    @Schema(description = "文件唯一标识")
    private Long fileId;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String originalName;

    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    /**
     * 文件url
     */
    @Schema(description = "文件url")
    private String fileUrl;

    /**
     * 文件哈希值(SHA256)
     */
    @Schema(description = "文件哈希值(SHA256)")
    private String fileHash;

    /**
     * MIME类型
     */
    @Schema(description = "MIME类型")
    private String mimeType;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 文件状态
     */
    @Schema(description = "文件状态")
    private String status;

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
     * 图片宽度
     */
    @Schema(description = "图片宽度")
    private Integer width;

    /**
     * 图片高度
     */
    @Schema(description = "图片高度")
    private Integer height;

    /**
     * 视频/音频时长(秒)
     */
    @Schema(description = "视频/音频时长(秒)")
    private Integer duration;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /**
     * 查看次数
     */
    @Schema(description = "查看次数")
    private Integer viewCount;

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessedAt;

    /**
     * 文件标签
     */
    @Schema(description = "文件标签")
    private String[] tags;

    /**
     * 文件描述
     */
    @Schema(description = "文件描述")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称")
    private String userName;
} 