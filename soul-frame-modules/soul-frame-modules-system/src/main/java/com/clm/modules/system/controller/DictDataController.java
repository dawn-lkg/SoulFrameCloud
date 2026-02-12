package com.clm.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.annotation.Log;
import com.clm.modules.system.domain.entity.DictData;
import com.clm.modules.system.domain.param.DictDataQueryParam;
import com.clm.modules.system.service.DictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据 控制层
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
@Validated
public class DictDataController extends BaseController {

    private final DictDataService dictDataService;

    @Operation(summary = "查询字典数据列表")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/page")
    public Result<Page<DictData>> page(DictDataQueryParam param) {
        Page<DictData> pageResult = dictDataService.selectDictDataPage(param);
        return success(pageResult);
    }

    @Operation(summary = "查询字典数据列表")
    @Log(value = "查询字典数据列表", businessType = BusinessType.QUERY)
    @GetMapping("/list")
    public Result<List<DictData>> list(DictDataQueryParam param) {
        List<DictData> list = dictDataService.selectDictDataList(param);
        return success(list);
    }

    @Operation(summary = "查询字典数据详情")
    @Log(value = "查询字典数据详情", businessType = BusinessType.QUERY)
    @GetMapping(value = "/{dictCode}")
    public Result<DictData> getInfo(@PathVariable("dictCode") Long dictCode) {
        return success(dictDataService.getById(dictCode));
    }

    @Operation(summary = "查询字典数据列表")
    @Log(value = "查询字典数据列表", businessType = BusinessType.QUERY)
    @GetMapping(value = "/type/{dictType}")
    public Result<List<DictData>> dictType(@PathVariable("dictType") String dictType) {
        List<DictData> data = dictDataService.selectDictDataByType(dictType);
        return success(data);
    }

    @Operation(summary = "查询字典数据详情")
    @Log(value = "新增字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<?> add(@RequestBody @Validated(Insert.class) DictData dict) {
        dictDataService.addDictData(dict);
        return success();
    }

    @Operation(summary = "修改字典数据")
    @Log(value = "修改字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<?> update(@RequestBody @Validated(Update.class) DictData dict) {
        dictDataService.updateDictData(dict);
        return success();
    }

    @Operation(summary = "删除字典数据")
    @Log(value = "删除字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable("id") Long id) {
        dictDataService.removeById(id);
        return success();
    }
} 