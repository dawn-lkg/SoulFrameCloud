package com.clm.modules.system.controller;

import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.annotation.Log;
import com.clm.modules.system.domain.dto.DeptDTO;
import com.clm.modules.system.domain.param.DeptQueryParam;
import com.clm.modules.system.domain.vo.DeptSelectVO;
import com.clm.modules.system.domain.vo.DeptVO;
import com.clm.modules.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门表(Dept)表控制层
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class DeptController extends BaseController {

    private final DeptService deptService;

    @Operation(summary = "获取部门列表")
    @Log(businessType = BusinessType.QUERY)
//    @SaCheckPermission("system:dept:query")
    @GetMapping("/list")
    public Result<List<DeptVO>> list(DeptQueryParam param) {
        List<DeptVO> depts = deptService.selectDeptList(param);
        return success(deptService.buildDeptTree(depts, 0L));
    }


    @Operation(summary = "获取部门详细信息")
    @Log(businessType = BusinessType.QUERY)
//    @SaCheckPermission("system:dept:query")
    @GetMapping("/{deptId}")
    public Result<DeptVO> info(@Schema(description = "部门ID") @PathVariable Long deptId) {
        return success(deptService.getDeptById(deptId));
    }

    @Operation(summary = "新增部门")
    @Log(businessType = BusinessType.INSERT)
//    @SaCheckPermission("system:dept:add")
    @PostMapping
    public Result<?> add(@RequestBody @Valid DeptDTO deptDTO) {
        deptService.insertDept(deptDTO);
        return success();
    }

    @Operation(summary = "修改部门")
    @Log(businessType = BusinessType.UPDATE)
//    @SaCheckPermission("system:dept:edit")
    @PutMapping
    public Result<?> update(@RequestBody @Valid DeptDTO deptDTO) {
        deptService.updateDept(deptDTO);
        return success();
    }

    @Operation(summary = "删除部门")
    @Log(businessType = BusinessType.DELETE)
//    @SaCheckPermission("system:dept:remove")
    @DeleteMapping("/{deptId}")
    public Result<?> remove(@Schema(description = "部门ID") @PathVariable Long deptId) {
        deptService.deleteDeptById(deptId);
        return success();
    }

    @Operation(summary = "检查部门名称是否唯一")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/checkDeptNameUnique")
    public Result<Boolean> checkDeptNameUnique(
            @Schema(description = "部门名称") @RequestParam String deptName,
            @Schema(description = "父部门ID") @RequestParam Long parentId,
            @Schema(description = "部门ID") @RequestParam(required = false) Long deptId) {
        return success(deptService.checkDeptNameUnique(deptName, parentId, deptId));
    }

    @Operation(summary = "获取部门下拉列表")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/select")
    public Result<List<DeptSelectVO>> select() {
        return success(deptService.buildDeptSelectTree(deptService.getDeptSelectList(), 0L));
    }
}
