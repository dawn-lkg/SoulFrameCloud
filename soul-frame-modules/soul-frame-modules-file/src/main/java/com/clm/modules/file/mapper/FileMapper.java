package com.clm.modules.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.modules.file.domain.entity.File;
import com.clm.modules.file.domain.param.FileQueryParam;
import com.clm.modules.file.domain.vo.FileVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件Mapper接口
 *
 * @author 陈黎明
 * @date 2025/3/10
 */
public interface FileMapper extends BaseMapper<File> {

    /**
     * 分页查询文件列表
     *
     * @param page 分页参数
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<FileVO> selectFilePage(@Param("page") Page<FileVO> page, @Param("param") FileQueryParam param);

    /**
     * 查询文件列表
     * @param param 查询参数
     * @return 文件列表
     */
    List<FileVO> selectFileList(@Param("param") FileQueryParam param);
    
    /**
     * 更新文件访问计数
     *
     * @param fileId 文件ID
     * @param type 计数类型 (view/download)
     * @return 影响行数
     */
    int updateAccessCount(@Param("fileId") Long fileId, @Param("type") String type);
} 