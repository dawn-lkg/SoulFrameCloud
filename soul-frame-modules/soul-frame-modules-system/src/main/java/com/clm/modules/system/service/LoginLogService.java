package com.clm.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.common.core.domain.BasePageParam;
import com.clm.modules.system.domain.entity.LoginLog;
import com.clm.modules.system.domain.param.LoginLogQueryParam;
import com.clm.modules.system.domain.vo.LoginLogVO;

import java.util.List;

/**
 * 系统访问记录(LoginLog)表服务接口
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
public interface LoginLogService extends IService<LoginLog> {
    
    /**
     * 分页查询登录日志
     *
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<LoginLogVO> pageLoginLog(LoginLogQueryParam param);
    
    /**
     * 新增登录日志
     *
     * @param loginLog 登录日志信息
     * @return 结果
     */
    boolean insertLoginLog(LoginLog loginLog);

    /**
     * 新增登录日志（异步）
     *
     * @param loginLog 登录日志信息
     */
    void insertLoginLogAsync(LoginLog loginLog);

    /**
     * 批量删除登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     */
    void deleteLoginLogByIds(List<Long> infoIds);
    
    /**
     * 查询登录日志详细
     *
     * @param infoId 访问ID
     * @return 登录日志对象
     */
    LoginLogVO getLoginLogById(Long infoId);
    
    /**
     * 清空登录日志
     */
    void cleanLoginLog();
    
    /**
     * 导出登录日志
     * @param param 查询条件
     */
    void exportLoginLog(LoginLogQueryParam param);
    
    /**
     * 记录登录成功日志
     * 
     * @param username 用户名
     * @param userId 用户ID
     * @param message 消息内容
     */
    void recordLoginSuccess(String username, Long userId, String message);
    
    /**
     * 记录登录失败日志
     * 
     * @param username 用户名
     * @param message 消息内容
     */
    void recordLoginFail(String username, String message);

    /**
     * 获取当前用户登录日志
     *
     * @return 登录日志列表
     */
    IPage<LoginLogVO> getCurrentUserLoginLog(BasePageParam param);
}