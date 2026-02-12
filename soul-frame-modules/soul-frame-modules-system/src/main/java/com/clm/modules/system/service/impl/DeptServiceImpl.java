package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.modules.system.domain.dto.DeptDTO;
import com.clm.modules.system.domain.entity.Dept;
import com.clm.modules.system.domain.param.DeptQueryParam;
import com.clm.modules.system.domain.vo.DeptSelectVO;
import com.clm.modules.system.domain.vo.DeptVO;
import com.clm.modules.system.mapper.DeptMapper;
import com.clm.modules.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 部门表(Dept)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Service("deptService")
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<DeptVO> selectDeptList(DeptQueryParam param) {
        return baseMapper.selectDeptList(param);
    }

    @Override
    public List<DeptVO> buildDeptTree(List<DeptVO> depts, Long parentId) {
        List<DeptVO> returnList = new ArrayList<>();
        depts.forEach(dept -> {
            if (Objects.equals(dept.getParentId(), parentId)) {
                dept.setChildren(buildDeptTree(depts, dept.getDeptId()));
                returnList.add(dept);
            }
        });
        return returnList;
    }

    @Override
    public List<DeptVO> buildDeptTree(List<DeptVO> depts) {
        List<DeptVO> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(DeptVO::getDeptId).toList();
        for (DeptVO dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<DeptVO> list, DeptVO t) {
        // 得到子节点列表
        List<DeptVO> childList = getChildList(list, t);
        t.setChildren(childList);
        for (DeptVO tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<DeptVO> getChildList(List<DeptVO> list, DeptVO t) {
        List<DeptVO> tlist = new ArrayList<>();
        for (DeptVO n : list) {
            if (n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<DeptVO> list, DeptVO t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    public DeptVO getDeptById(Long deptId) {
        return baseMapper.selectDeptById(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDept(DeptDTO deptDTO) {
        if (checkDeptNameUnique(deptDTO.getDeptName(), deptDTO.getParentId(), null)) {
            throw new BaseException(HttpCodeEnum.DEPT_NAME_EXIST);
        }

        Dept parent = null;
        if (deptDTO.getParentId() != 0) {
            parent = getById(deptDTO.getParentId());
            if (parent == null) {
                throw new BaseException(HttpCodeEnum.PARENT_DEPT_NOT_EXIST);
            }
        }

        Dept dept = new Dept();
        BeanUtils.copyProperties(deptDTO, dept);

        // 设置祖级列表
        if (parent != null) {
            dept.setAncestors(parent.getAncestors() + "," + parent.getDeptId());
        } else {
            dept.setAncestors("0");
        }

        save(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(DeptDTO deptDTO) {
        if (checkDeptNameUnique(deptDTO.getDeptName(), deptDTO.getParentId(), deptDTO.getDeptId())) {
            throw new BaseException(HttpCodeEnum.DEPT_NAME_EXIST);
        }

        Dept oldDept = getById(deptDTO.getDeptId());
        if (oldDept == null) {
            throw new BaseException(HttpCodeEnum.DEPT_NOT_EXIST);
        }

        if (deptDTO.getParentId().equals(deptDTO.getDeptId())) {
            throw new BaseException(HttpCodeEnum.PARENT_DEPT_CANNOT_BE_SELF);
        }

        // 不能将部门的父部门设置为自己的子部门
        Dept parent = null;
        if (deptDTO.getParentId() != 0) {
            parent = getById(deptDTO.getParentId());
            if (parent == null) {
                throw new BaseException(HttpCodeEnum.PARENT_DEPT_NOT_EXIST);
            }

            if (parent.getAncestors().contains(deptDTO.getDeptId().toString())) {
                throw new BaseException(HttpCodeEnum.PARENT_DEPT_CANNOT_BE_CHILD);
            }
        }

        Dept dept = new Dept();
        BeanUtils.copyProperties(deptDTO, dept);

        // 更新祖级列表
        if (parent != null && !oldDept.getParentId().equals(deptDTO.getParentId())) {
            dept.setAncestors(parent.getAncestors() + "," + parent.getDeptId());

            // 更新子部门的祖级列表
            updateChildDeptAncestors(dept);
        }

        updateById(dept);
    }

    /**
     * 更新子部门的祖级列表
     */
    private void updateChildDeptAncestors(Dept dept) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Dept::getAncestors, dept.getDeptId().toString() + ",")
                .or()
                .eq(Dept::getParentId, dept.getDeptId());

        List<Dept> childDepts = list(wrapper);
        for (Dept child : childDepts) {
            child.setAncestors(dept.getAncestors() + "," + dept.getDeptId());
        }

        if (!childDepts.isEmpty()) {
            updateBatchById(childDepts);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeptById(Long deptId) {
        // 判断是否存在子部门
        if (baseMapper.hasChildByDeptId(deptId) > 0) {
            throw new BaseException(HttpCodeEnum.DEPT_EXIST_CHILD);
        }

        // 判断是否存在用户
        if (baseMapper.checkDeptExistUser(deptId) > 0) {
            throw new BaseException(HttpCodeEnum.DEPT_EXIST_USER);
        }

        removeById(deptId);
    }

    @Override
    public boolean checkDeptNameUnique(String deptName, Long parentId, Long deptId) {
        return lambdaQuery().eq(Dept::getDeptName, deptName)
                .eq(Dept::getParentId, parentId)
                .ne(Objects.nonNull(deptId), Dept::getDeptId, deptId)
                .exists();
    }

    @Override
    public List<DeptSelectVO> getDeptSelectList() {
        return baseMapper.getDeptSelectList();
    }

    @Override
    public List<DeptSelectVO> buildDeptSelectTree(List<DeptSelectVO> depts, Long parentId) {
        List<DeptSelectVO> returnList = new ArrayList<>();
        for (DeptSelectVO dept : depts) {
            if (dept.getParentId().equals(parentId)) {
                List<DeptSelectVO> deptSelectVOS = buildDeptSelectTree(depts, dept.getId());
                Integer userCount = dept.getUserCount() + deptSelectVOS.stream().mapToInt(DeptSelectVO::getUserCount).sum();
                dept.setUserCount(userCount);
                dept.setChildren(deptSelectVOS);
                returnList.add(dept);
            }
        }
        return returnList;
    }
}
