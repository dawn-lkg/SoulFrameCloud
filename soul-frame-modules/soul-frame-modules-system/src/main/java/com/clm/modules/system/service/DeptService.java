package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.dto.DeptDTO;
import com.clm.modules.system.domain.entity.Dept;
import com.clm.modules.system.domain.param.DeptQueryParam;
import com.clm.modules.system.domain.vo.DeptSelectVO;
import com.clm.modules.system.domain.vo.DeptVO;

import java.util.List;

/**
 * 部门表(Dept)表服务接口
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
public interface DeptService extends IService<Dept> {

    /**
     * 查询部门列表
     *
     * @param param 查询参数
     * @return 部门列表
     */
    List<DeptVO> selectDeptList(DeptQueryParam param);

    /**
     * 构建部门树结构
     *
     * @param depts    部门列表
     * @param parentId 父部门ID
     * @return 树结构列表
     */
    List<DeptVO> buildDeptTree(List<DeptVO> depts, Long parentId);

    /**
     * 构建部门树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    List<DeptVO> buildDeptTree(List<DeptVO> depts);

    /**
     * 根据ID查询部门信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    DeptVO getDeptById(Long deptId);

    /**
     * 新增部门
     *
     * @param deptDTO 部门信息
     * @return 结果
     */
    void insertDept(DeptDTO deptDTO);

    /**
     * 修改部门
     *
     * @param deptDTO 部门信息
     * @return 结果
     */
    void updateDept(DeptDTO deptDTO);

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 结果
     */
    void deleteDeptById(Long deptId);

    /**
     * 检查部门名称是否唯一
     *
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @param deptId   部门ID
     * @return 结果
     */
    boolean checkDeptNameUnique(String deptName, Long parentId, Long deptId);

    /**
     * 获取部门下拉列表
     *
     * @return 部门下拉列表
     */
    List<DeptSelectVO> getDeptSelectList();

    /**
     * 构建部门下拉树结构
     *
     * @param depts    部门列表
     * @param parentId 父部门ID
     * @return 树结构列表
     */
    List<DeptSelectVO> buildDeptSelectTree(List<DeptSelectVO> depts, Long parentId);
}
