package com.clm.modules.system.service.impl;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.clm.common.core.domain.entity.User;
import com.clm.common.core.utils.IpUtils;
import com.clm.common.core.utils.ServletUtils;
import com.clm.common.core.utils.UserAgentUtils;
import com.clm.common.redis.utils.RedisUtils;
import com.clm.modules.system.domain.entity.OnlineUser;
import com.clm.modules.system.domain.param.OnlineUserQueryParam;
import com.clm.modules.system.service.OnlineUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

;

/**
 * 在线用户服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements OnlineUserService {

    private final RedisUtils redisUtils;
    private final SaTokenDao saTokenDao;
    
    @Value("${ip.xdbPath}")
    private String xdbPath;
    
    /**
     * 在线用户缓存前缀
     */
    private static final String ONLINE_USER_KEY_PREFIX = "online_user:";
    
    /**
     * 在线用户缓存过期时间（小时）
     */
    private static final long ONLINE_USER_TIMEOUT = 12;

    @Override
    public List<OnlineUser> listOnlineUsers(OnlineUserQueryParam param) {
        // 获取所有token
        Set<String> keys = redisUtils.keys(ONLINE_USER_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有在线用户
        List<OnlineUser> onlineUsers = keys.stream()
                .map(key -> redisUtils.get(key, OnlineUser.class))
                .filter(user -> user != null)
                .collect(Collectors.toList());
        
        // 根据查询条件过滤
        if (param != null) {
            if (StrUtil.isNotBlank(param.getUserName())) {
                onlineUsers = onlineUsers.stream()
                        .filter(user -> user.getUserName().contains(param.getUserName()))
                        .collect(Collectors.toList());
            }
            
            if (StrUtil.isNotBlank(param.getIpaddr())) {
                onlineUsers = onlineUsers.stream()
                        .filter(user -> user.getIpaddr().contains(param.getIpaddr()))
                        .collect(Collectors.toList());
            }
        }
        
        return onlineUsers;
    }

    @Override
    public long getOnlineUserCount() {
        Set<String> keys = redisUtils.keys(ONLINE_USER_KEY_PREFIX + "*");
        return keys.size();
    }

    @Override
    public boolean forceLogout(String userId) {
        if (Objects.isNull(userId)) {
            return false;
        }
        
        // 从缓存中获取在线用户
        OnlineUser onlineUser = getOnlineUser(userId);
        if (onlineUser == null) {
            return false;
        }
        
        try {
            // 删除在线用户缓存
            redisUtils.delete(ONLINE_USER_KEY_PREFIX + userId);
            
            // 强制注销用户
            StpUtil.kickout(userId);
            
            return true;
        } catch (Exception e) {
            log.error("强制退出用户失败", e);
            return false;
        }
    }

    @Override
    public void setOnlineUser(User user, String tokenId) {
        if (user == null || StrUtil.isBlank(tokenId)) {
            return;
        }
        
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            String userAgent = UserAgentUtils.getUserAgent(request);
            String ip = IpUtils.getIpAddr(request);
            
            OnlineUser onlineUser = new OnlineUser();
            onlineUser.setTokenId(tokenId);
            onlineUser.setUserId(user.getUserId());
            onlineUser.setUserName(user.getUserName());
            onlineUser.setNickName(user.getNickName());
            onlineUser.setIpaddr(ip);
            onlineUser.setLoginLocation(IpUtils.getCityInfoByVectorIndex(ip, xdbPath));
            onlineUser.setBrowser(UserAgentUtils.getBrowser(userAgent));
            onlineUser.setOs(UserAgentUtils.getOs(userAgent));
            onlineUser.setLoginTime(new Date());
            
            // 存储在线用户信息到Redis
            redisUtils.set(ONLINE_USER_KEY_PREFIX + user.getUserId(), onlineUser, ONLINE_USER_TIMEOUT, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("设置在线用户信息失败", e);
        }
    }

    @Override
    public OnlineUser getOnlineUser(String userId) {
        if (Objects.isNull(userId)) {
            return null;
        }

        return redisUtils.get(ONLINE_USER_KEY_PREFIX + userId, OnlineUser.class);
    }
} 