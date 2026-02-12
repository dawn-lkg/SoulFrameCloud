package com.clm.modules.file.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.clm.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文件表
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "sys_file", autoResultMap = true)
@Schema(description = "文件")
public class File extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识
     */
    @TableId(value = "file_id", type = IdType.AUTO)
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
     * 文件编码
     */
    @Schema(description = "文件编码")
    private String encoding;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    private String storagePath;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 文件所有者ID
     */
    @Schema(description = "文件所有者ID")
    private Long ownerId;

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
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer versionNumber;

    /**
     * 是否最新版本
     */
    @Schema(description = "是否最新版本")
    private Boolean isLatestVersion;

    /**
     * 上传IP地址
     */
    @Schema(description = "上传IP地址")
    private String uploadIp;

    /**
     * 上传用户代理
     */
    @Schema(description = "上传用户代理")
    private String uploadUserAgent;

    /**
     * 上传来源
     */
    @Schema(description = "上传来源")
    private String uploadSource;

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
     * 扩展元数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "扩展元数据")
    private Map<String, Object> metadata;

    /**
     * 文件标签
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "文件标签")
    private String[] tags;

    /**
     * 文件描述
     */
    @Schema(description = "文件描述")
    private String description;

    /**
     * 预览地址（PDF/HTML）
     */
    @Schema(description = "预览地址（PDF/HTML）")
    private String previewUrl;

    /**
     * 缩略图地址
     */
    @Schema(description = "缩略图地址")
    private String thumbnailUrl;

    /**
     * 文档页数
     */
    @Schema(description = "文档页数")
    private Integer pageCount;

    /**
     * 转换状态：0-未转换 1-转换中 2-成功 3-失败
     */
    @Schema(description = "转换状态：0-未转换 1-转换中 2-成功 3-失败")
    private Integer convertStatus;

    /**
     * 转换失败原因
     */
    @Schema(description = "转换失败原因")
    private String convertErrorMsg;

    /**
     * 转换完成时间
     */
    @Schema(description = "转换完成时间")
    private LocalDateTime convertTime;

    /**
     * OCR识别文本
     */
    @Schema(description = "OCR识别文本")
    private String ocrText;

    /**
     * OCR状态：0-未识别 1-识别中 2-成功 3-失败
     */
    @Schema(description = "OCR状态：0-未识别 1-识别中 2-成功 3-失败")
    private Integer ocrStatus;

    /**
     * OCR完成时间
     */
    @Schema(description = "OCR完成时间")
    private LocalDateTime ocrTime;

    /**
     * 是否支持协同编辑
     */
    @Schema(description = "是否支持协同编辑")
    private Boolean isCollaborative;

    /**
     * 当前编辑锁持有者ID
     */
    @Schema(description = "当前编辑锁持有者ID")
    private Long lockUserId;

    /**
     * 锁定时间
     */
    @Schema(description = "锁定时间")
    private LocalDateTime lockTime;

    /**
     * 父文件ID（用于版本管理）
     */
    @Schema(description = "父文件ID（用于版本管理）")
    private Long parentFileId;

    /**
     * 版本变更说明
     */
    @Schema(description = "版本变更说明")
    private String changeLog;
} 