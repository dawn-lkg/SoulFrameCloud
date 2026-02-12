package com.clm.modules.file.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件信息VO类
 *
 * @author 陈黎明
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件存储名称
     */
    private String fileKey;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件URL
     */
    private String url;
} 