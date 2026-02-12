package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.modules.system.domain.entity.Dept;
import com.clm.modules.system.domain.param.DeptQueryParam;
import com.clm.modules.system.domain.vo.DeptSelectVO;
import com.clm.modules.system.domain.vo.DeptVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门表(Dept)表数据库访问层
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 查询部门列表
     *
     * @param param 查询参数
     * @return 部门列表
     */
    List<DeptVO> selectDeptList(DeptQueryParam param);

    /**
     * 根据ID查询部门信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    DeptVO selectDeptById(Long deptId);

    /**
     * 是否存在子部门
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int checkDeptExistUser(Long deptId);

    /**
     * 获取部门下拉列表
     *
     * @return 部门下拉列表
     */
    List<DeptSelectVO> getDeptSelectList();
}
