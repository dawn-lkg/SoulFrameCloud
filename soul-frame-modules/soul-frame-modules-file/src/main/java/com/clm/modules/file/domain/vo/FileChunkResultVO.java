package com.clm.modules.file.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 文件分片上传结果VO
 *
 * @author 陈黎明
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileChunkResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否跳过上传
     */
    private boolean skipUpload;

    /**
     * 已上传分片列表
     */
    private List<Integer> uploadedChunks;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 返回文件信息，合并完成时才有
     */
    private FileInfoVO fileInfo;
} 