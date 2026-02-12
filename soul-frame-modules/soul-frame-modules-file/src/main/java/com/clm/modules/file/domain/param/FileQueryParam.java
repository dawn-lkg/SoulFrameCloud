package com.clm.modules.file.domain.param;

import com.clm.common.core.domain.BasePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 文件查询参数
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
@Data
@Schema(description = "文件查询参数")
public class FileQueryParam extends BasePageParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 原始文件名称
     */
    @Schema(description = "原始文件名称")
    private String originalName;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private List<String> fileExtensionList;

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
     * 上传开始时间
     */
    @Schema(description = "上传开始时间")
    private String beginTime;

    /**
     * 上传结束时间
     */
    @Schema(description = "上传结束时间")
    private String endTime;
    
    /**
     * 文件所有者ID
     */
    @Schema(description = "文件所有者ID")
    private Long ownerId;
} 