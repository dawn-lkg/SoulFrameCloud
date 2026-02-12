package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.common.core.domain.entity.User;
import com.clm.modules.system.domain.param.UserQueryParam;
import com.clm.modules.system.domain.vo.UserPageVO;
import com.clm.modules.system.domain.vo.UserSelectVO;
import com.clm.modules.system.domain.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息表(User)表数据库访问层
 *
 * @author 陈黎明
 * @since 2025-03-07 14:31:36
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户
     *
     * @param page  分页参数
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<UserPageVO> selectUserPage(Page<UserPageVO> page, @Param("param") UserQueryParam param);
    
    /**
     * 根据ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserVO selectUserById(@Param("userId") Long userId);

    /**
     * 查询用户下拉列表
     *
     * @return 用户下拉列表
     */
    List<UserSelectVO> selectSelectUserList(@Param("param") UserQueryParam param);
}

