package com.clm.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.domain.entity.User;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.common.core.utils.IpUtils;
import com.clm.common.core.utils.NicknameGenerator;
import com.clm.common.core.utils.PasswordUtils;
import com.clm.common.core.utils.ServletUtils;
import com.clm.common.security.utils.AuthenticationUtil;
import com.clm.modules.system.domain.dto.UserDTO;
import com.clm.modules.system.domain.param.UserQueryParam;
import com.clm.modules.system.domain.vo.UserPageVO;
import com.clm.modules.system.domain.vo.UserSelectVO;
import com.clm.modules.system.domain.vo.UserVO;
import com.clm.modules.system.mapper.UserMapper;
import com.clm.modules.system.service.ConfigService;
import com.clm.modules.system.service.UserRoleService;
import com.clm.modules.system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息表(User)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-07 14:31:37
 */

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleService userRoleService;

    private final ConfigService configService;

//    private final FileService fileService;

    @Override
    public IPage<UserPageVO> getUserPage(UserQueryParam param) {
        Page<UserPageVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        IPage<UserPageVO> userPage = baseMapper.selectUserPage(page, param);
        if (userPage.getRecords().isEmpty()) {
            return userPage;
        }
        List<Long> userIdList =  userPage.getRecords().stream().map(UserPageVO::getUserId).collect(Collectors.toList());
        Map <Long, List<Long>> roleMap = userRoleService.getUserRoleMapByUserIds(userIdList);
        userPage.getRecords().forEach(userVO -> userVO.setRoleIds(roleMap.getOrDefault(userVO.getUserId(), List.of())));
        return userPage;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BaseException("用户ID无效", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        UserVO userVO = baseMapper.selectUserById(userId);
        if (userVO == null) {
            throw new BaseException("用户不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        return userVO;
    }

    @Override
    public void updateUserInfo(UserDTO userDTO) {
        userDTO.setUserId(AuthenticationUtil.getUserId());
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        if (!updateById(user)) {
            throw new BaseException("更新用户信息失败", HttpCodeEnum.ERROR.getCode());
        }
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        User user = getById(AuthenticationUtil.getUserId());
        if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
            throw new BaseException("旧密码错误", HttpCodeEnum.PASSWORD_ERROR.getCode());
        }
        user.setPassword(PasswordUtils.encryptPassword(newPassword));
        if (!updateById(user)) {
            throw new BaseException("更新用户信息失败", HttpCodeEnum.ERROR.getCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        Long count = lambdaQuery().eq(User::getUserName, userDTO.getUserName()).count();
        if (count > 0) {
            throw new BaseException("用户名已存在", HttpCodeEnum.ACCOUNT_EXIST.getCode());
        }
        
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 如果昵称为空，则生成随机昵称
        if (!StringUtils.hasText(user.getNickName())) {
            String randomNickname = NicknameGenerator.generateNicknameWithNumber();
            user.setNickName(randomNickname);
        }
        
        // 设置初始密码并加密
        String initPassword = "123456";
        user.setPassword(PasswordUtils.encryptPassword(initPassword));
        if(!save(user)){
            throw new BaseException("新增用户失败", HttpCodeEnum.ERROR.getCode());
        }

        // 保存用户角色关联信息
        userRoleService.saveUserRole(user.getUserId(), userDTO.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        if (userDTO.getUserId() == null) {
            throw new BaseException("用户ID不能为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        
        // 检查用户是否存在
        User existUser = getById(userDTO.getUserId());
        if (existUser == null) {
            throw new BaseException("用户不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        
        // 检查用户名是否重复（排除自身）
        if (!existUser.getUserName().equals(userDTO.getUserName())) {
            Long count = lambdaQuery().eq(User::getUserName, userDTO.getUserName()).count();
            if (count > 0) {
                throw new BaseException("用户名已存在", HttpCodeEnum.ACCOUNT_EXIST.getCode());
            }
        }
        
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 如果昵称为空，则生成随机昵称
        if (!StringUtils.hasText(user.getNickName())) {
            String randomNickname = NicknameGenerator.generateNicknameWithNumber();
            user.setNickName(randomNickname);
            log.info("用户 {} 生成随机昵称: {}", user.getUserName(), randomNickname);
        }
        
        // 如果密码为空，则保持原密码不变
        if (!StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(null);
        } else {
            // 密码加密处理
            user.setPassword(PasswordUtils.encryptPassword(userDTO.getPassword()));
        }
        
        if(!updateById(user)){
            throw new BaseException("更新用户失败", HttpCodeEnum.ERROR.getCode());
        }

        // 保存用户角色关联信息
        userRoleService.saveUserRole(user.getUserId(), userDTO.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BaseException("用户ID无效", HttpCodeEnum.BAD_REQUEST.getCode());
        }

        User user = getById(userId);
        if (user == null) {
            throw new BaseException("用户不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }

        if (!removeById(userId)) {
            throw new BaseException("删除用户失败", HttpCodeEnum.ERROR.getCode());
        }

        // 删除用户角色关联信息
        userRoleService.removeByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUser(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BaseException("用户ID列表不能为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        if (!removeByIds(userIds)) {
            throw new BaseException("批量删除用户失败", HttpCodeEnum.ERROR.getCode());
        }
    }

    @Override
    public User getUserByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            throw new BaseException("用户名不能为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        return lambdaQuery().eq(User::getUserName, username).last("limit 1").one();
    }

    @Override
    public void updateLoginInfo(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BaseException("用户ID无效", HttpCodeEnum.BAD_REQUEST.getCode());
        }

        HttpServletRequest request = ServletUtils.getRequest();
        String ipAddr = IpUtils.getIpAddr(request);
        lambdaUpdate()
                .set(User::getLoginIp, ipAddr)
                .set(User::getLoginDate, LocalDateTime.now())
                .eq(User::getUserId, userId)
                .update();
    }

    @Override
    public boolean checkUsernameExists(String username, Long userId) {
        if (StrUtil.isBlank(username)) {
            return false;
        }
        return lambdaQuery()
                .eq(User::getUserName, username)
                .ne(userId != null, User::getUserId, userId)
                .exists();
    }

    @Override
    public void resetPassword(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BaseException("用户ID无效", HttpCodeEnum.BAD_REQUEST.getCode());
        }

        String initPassword = "123456";
        String configPassword = configService.getValueByKey("sys.user.defaultPassword", String.class);
        initPassword = StrUtil.isNotBlank(configPassword) ? configPassword : initPassword;

        if (StrUtil.isBlank(initPassword)) {
            throw new BaseException("初始密码配置为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }

        boolean update = lambdaUpdate()
                .set(User::getPassword, PasswordUtils.encryptPassword(initPassword))
                .eq(User::getUserId, userId)
                .update();
        if (!update) {
            throw new BaseException("重置密码失败", HttpCodeEnum.ERROR.getCode());
        }
    }

    @Override
    public void updateAvatar(MultipartFile file) {
//        String url = fileService.uploadFile(file);
//        boolean update = lambdaUpdate().eq(User::getUserId, LoginHelper.getUserId())
//                .set(User::getAvatar, url).update();
//        if (!update) {
//            throw new BaseException("更新头像失败", HttpCodeEnum.ERROR.getCode());
//        }
    }

    @Override
    public List<UserSelectVO> getSelectUserList(UserQueryParam param) {
        return baseMapper.selectSelectUserList(param);
    }
}
