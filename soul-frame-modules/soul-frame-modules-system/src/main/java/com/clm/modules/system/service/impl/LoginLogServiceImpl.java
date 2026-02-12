package com.clm.modules.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.domain.BasePageParam;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.common.security.utils.AuthenticationUtil;
import com.clm.common.core.utils.IpUtils;
import com.clm.common.core.utils.ServletUtils;
import com.clm.common.core.utils.UserAgentUtils;
import com.clm.modules.system.domain.entity.LoginLog;
import com.clm.modules.system.domain.param.LoginLogQueryParam;
import com.clm.modules.system.domain.vo.LoginLogExportVO;
import com.clm.modules.system.domain.vo.LoginLogVO;
import com.clm.modules.system.mapper.LoginLogMapper;
import com.clm.modules.system.service.LoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * 系统访问记录(LoginLog)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Service("loginLogService")
@RequiredArgsConstructor
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    
    @Value("${ip.xdbPath}")
    private String xdbPath;
    
    @Override
    public IPage<LoginLogVO> pageLoginLog(LoginLogQueryParam param) {
        Page<LoginLogVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        IPage<LoginLogVO> pageResult = baseMapper.selectLoginLogPage(page, param);
        
        // 填充状态描述
        for (LoginLogVO vo : pageResult.getRecords()) {
            processLoginLogVO(vo);
        }
        
        return pageResult;
    }

    @Override
    public boolean insertLoginLog(LoginLog loginLog) {
        return save(loginLog);
    }

    @Async
    @Override
    public void insertLoginLogAsync(LoginLog loginLog) {
        save(loginLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLoginLogByIds(List<Long> infoIds) {
        if (infoIds == null || infoIds.isEmpty()) {
            throw new BaseException("登录日志ID不能为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        removeByIds(infoIds);
    }
    
    @Override
    public LoginLogVO getLoginLogById(Long infoId) {
        LoginLog loginLog = getById(infoId);
        if (loginLog == null) {
            throw new BaseException("登录日志不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        
        LoginLogVO vo = new LoginLogVO();
        BeanUtils.copyProperties(loginLog, vo);
        
        // 填充状态描述
        processLoginLogVO(vo);
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanLoginLog() {
        baseMapper.cleanLoginLog();
    }
    
    @Override
    public void exportLoginLog(LoginLogQueryParam param) {
        List<LoginLogVO> list = baseMapper.selectLoginLogList(param);
        List<LoginLogExportVO> exportVOList = list.stream().map(vo -> {
            LoginLogExportVO loginLogExportVO = new LoginLogExportVO();
            processLoginLogVO(vo);
            BeanUtils.copyProperties(vo, loginLogExportVO);
            return loginLogExportVO;
        }).toList();

        // 获取返回对象
        HttpServletResponse response = ServletUtils.getResponse();

        if(response == null){
            throw new BaseException("返回对象为空", HttpCodeEnum.ERROR.getCode());
        }

        response.setCharacterEncoding("utf-8");
        String fileName = "登录日志_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        try(OutputStream os = response.getOutputStream()){
            EasyExcel.write(os).sheet("登录日志").head(LoginLogExportVO.class).doWrite(exportVOList);
        } catch (Exception e){
            throw new BaseException("导出登录日志失败", HttpCodeEnum.ERROR.getCode());
        }

    }
    
    @Override
    public void recordLoginSuccess(String username, Long userId, String message) {
        recordLoginInfo(username, userId, "0", message);
    }
    
    @Override
    public void recordLoginFail(String username, String message) {
        recordLoginInfo(username, null, "1", message);
    }

    @Override
    public IPage<LoginLogVO> getCurrentUserLoginLog(BasePageParam param) {
        LoginLogQueryParam loginLogQueryParam = new LoginLogQueryParam();
        loginLogQueryParam.setPageNum(param.getPageNum());
        loginLogQueryParam.setPageSize(param.getPageSize());
        loginLogQueryParam.setUserId(AuthenticationUtil.getUserId());
        return pageLoginLog(loginLogQueryParam);
    }

    /**
     * 填充登录日志视图对象的描述字段
     * 
     * @param vo 登录日志视图对象
     */
    private void processLoginLogVO(LoginLogVO vo) {
        // 处理状态描述
        if ("0".equals(vo.getStatus())) {
            vo.setStatusDesc("成功");
        } else if ("1".equals(vo.getStatus())) {
            vo.setStatusDesc("失败");
        } else {
            vo.setStatusDesc("未知");
        }
    }
    
    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param userId 用户ID
     * @param status 状态
     * @param message 消息内容
     */
    private void recordLoginInfo(String username, Long userId, String status, String message) {
        HttpServletRequest request = ServletUtils.getRequest();
        String userAgent = null;
        if (request != null) {
            userAgent = UserAgentUtils.getUserAgent(request);
        }
        String ip = IpUtils.getIpAddr(request);
        
        LoginLog loginLog = new LoginLog();
        loginLog.setUserName(username);
        loginLog.setUserId(userId);
        loginLog.setIpaddr(ip);
        loginLog.setLoginLocation(IpUtils.getCityInfoByVectorIndex(ip, xdbPath));
        loginLog.setBrowser(UserAgentUtils.getBrowser(userAgent));
        loginLog.setOs(UserAgentUtils.getOs(userAgent));
        loginLog.setStatus(status);
        loginLog.setMsg(message);
        loginLog.setLoginTime(new Date());
        
        // 异步保存登录日志
        insertLoginLogAsync(loginLog);
    }
} 