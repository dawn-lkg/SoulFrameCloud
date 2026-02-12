package com.clm.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.annotation.Log;
import com.clm.modules.system.domain.entity.DictType;
import com.clm.modules.system.domain.param.DictTypeQueryParam;
import com.clm.modules.system.service.DictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 字典类型 控制层
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
@Validated
public class DictTypeController extends BaseController {

    private final DictTypeService dictTypeService;

    @Operation(summary = "分页查询字典类型列表")
    @GetMapping("/page")
    @Log(businessType = BusinessType.QUERY)
    public Result<Page<DictType>> page(DictTypeQueryParam param) {
        return success(dictTypeService.selectDictTypePage(param));
    }

    @Operation(summary = "查询字典类型列表")
    @Log(value = "查询字典类型列表", businessType = BusinessType.QUERY)
    @GetMapping("/list")
    public Result<List<DictType>> list(DictTypeQueryParam param) {
        List<DictType> list = dictTypeService.selectDictTypeList(param);
        return success(list);
    }

    @Operation(summary = "查询字典类型详情")
    @Log(value = "查询字典类型详情", businessType = BusinessType.QUERY)
    @GetMapping(value = "/{dictId}")
    public Result<DictType> getInfo(@PathVariable Long dictId) {
        return success(dictTypeService.getById(dictId));
    }

    @Operation(summary = "新增字典类型")
    @Log(value = "新增字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<?> add(@RequestBody @Valid DictType dict) {
        dictTypeService.addDictType(dict);
        return success();
    }

    @Operation(summary = "修改字典类型")
    @Log(value = "修改字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<?> update(@RequestBody @Valid DictType dict) {
        dictTypeService.updateDictType(dict);
        return success();
    }

    @Operation(summary = "删除字典类型")
    @Log(value = "删除字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        dictTypeService.deleteDictTypeByIds(Collections.singletonList(id));
        return success();
    }


    @Operation(summary = "批量修改字典类型")
    @Log(value = "批量删除字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<?> remove(@RequestBody List<Long> dictIdList) {
        dictTypeService.deleteDictTypeByIds(dictIdList);
        return success();
    }

    @Operation(summary = "刷新字典缓存")
    @Log(value = "刷新字典缓存", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public Result<?> refreshCache() {
        dictTypeService.resetDictCache();
        return success();
    }
} 