package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.modules.system.domain.entity.LoginLog;
import com.clm.modules.system.domain.param.LoginLogQueryParam;
import com.clm.modules.system.domain.vo.LoginLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统访问记录(LoginLog)表数据库访问层
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
public interface LoginLogMapper extends BaseMapper<LoginLog> {
    
    /**
     * 分页查询登录日志
     *
     * @param page  分页参数
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<LoginLogVO> selectLoginLogPage(Page<LoginLogVO> page, @Param("param") LoginLogQueryParam param);
    
    /**
     * 查询登录日志列表
     *
     * @param param 查询参数
     * @return 日志列表
     */
    List<LoginLogVO> selectLoginLogList(@Param("param") LoginLogQueryParam param);
    
    /**
     * 清空登录日志
     */
    void cleanLoginLog();
} 