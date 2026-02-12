package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.modules.system.domain.entity.Config;
import com.clm.modules.system.domain.param.ConfigParam;
import com.clm.modules.system.domain.vo.ConfigVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置表 数据层
 *
 * @author 陈黎明
 * @date 2025-07-12 21:01:22
 */
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 分页查询系统配置表列表
     * 
     * @param page 分页参数
     * @param param 查询参数
     * @return 系统配置表集合
     */
    List<ConfigVO> selectConfigPage(@Param("page") Page<ConfigVO> page,@Param("param") ConfigParam param);
    
    /**
     * 查询系统配置表列表
     *
     * @param param 查询参数
     * @return 系统配置表集合
     */
    List<ConfigVO> selectConfigList(@Param("param") ConfigParam param);
}
