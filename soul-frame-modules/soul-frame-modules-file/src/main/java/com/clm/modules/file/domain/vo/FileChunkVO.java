package com.clm.modules.file.domain.vo;

import lombok.Data;

/**
 * 文件分片信息VO
 *
 * @author 陈黎明
 */
@Data
public class FileChunkVO {

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片索引
     */
    private Integer chunkNumber;

    /**
     * 当前分片大小
     */
    private Long chunkSize;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 当前分片数据
     */
    private byte[] bytes;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 自定义存储桶名称，可选
     */
    private String bucketName;
} 