package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.modules.system.domain.entity.RoleDept;
import com.clm.modules.system.mapper.RoleDeptMapper;
import com.clm.modules.system.service.RoleDeptService;
import org.springframework.stereotype.Service;

/**
 * 角色和部门关联表(RoleDept)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-08 11:02:08
 */
@Service("roleDeptService")
public class RoleDeptServiceImpl extends ServiceImpl<RoleDeptMapper, RoleDept> implements RoleDeptService {

}

