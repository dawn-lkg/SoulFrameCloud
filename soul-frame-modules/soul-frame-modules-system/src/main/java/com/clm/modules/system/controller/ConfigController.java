package com.clm.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.annotation.Log;
import com.clm.modules.system.domain.dto.ConfigDTO;
import com.clm.modules.system.domain.param.ConfigParam;
import com.clm.modules.system.domain.vo.ConfigVO;
import com.clm.modules.system.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置表 控制层
 *
 * @author 陈黎明
 * @date 2025-07-12 19:54:30
 */
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
@Validated
@Tag(name = "系统配置表管理", description = "系统配置表相关接口")
public class ConfigController extends BaseController {

    private final ConfigService configService;

    @Operation(summary = "获取系统配置表分页列表", description = "分页查询系统配置表信息")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/page")
    public Result<Page<ConfigVO>> page(ConfigParam param) {
        return success(configService.selectConfigPage(param));
    }
    
    @Operation(summary = "获取系统配置表列表", description = "查询系统配置表列表信息")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/list")
    public Result<List<ConfigVO>> list(ConfigParam param) {
        return success(configService.selectConfigList(param));
    }

    @Operation(summary = "获取系统配置表详情", description = "根据ID查询系统配置表详细信息")
    @Parameter(name = "id", description = "系统配置表ID", required = true)
    @Log(businessType = BusinessType.QUERY)
    @GetMapping(value = "/{id}")
    public Result<ConfigVO> getInfo(@PathVariable("id") Long id) {
        return success(configService.getByIdRel(id));
    }

    @Operation(summary = "新增系统配置", description = "新增系统配置表信息")
    @Log(businessType = BusinessType.INSERT)
    @PostMapping
    public Result<?> add(@Validated @RequestBody ConfigDTO dto) {
        configService.saveConfig(dto);
        return success();
    }

    @Operation(summary = "修改系统配置", description = "修改系统配置表信息")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<?> edit(@Validated @RequestBody ConfigDTO dto) {
        configService.updateConfig(dto);
        return success();
    }

    @Operation(summary = "删除系统配置", description = "根据ID删除系统配置表信息")
    @Parameter(name = "id", description = "系统配置表ID", required = true)
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable("id") Long id) {
        configService.deleteConfig(id);
        return success();
    }

    @Operation(summary = "批量删除系统配置", description = "根据ID列表批量删除系统配置表信息")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<?> batchRemove(@RequestBody @Validated List<Long> ids) {
        configService.removeByIds(ids);
        return success();
    }

    @Operation(summary = "获取系统配置（缓存）", description = "根据分组名称获取系统配置")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/configGroup")
    public Result<List<ConfigVO>> getConfigList(@RequestParam(value = "groupName") String groupName) {
        List<ConfigVO> list = configService.getConfigList(groupName);
        return success(list);
    }

    @Operation(summary = "获取系统配置 （缓存）", description = "根据键名获取系统配置")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/key/{configKey}")
    public Result<ConfigVO> getConfigByKey(@PathVariable("configKey") String configKey) {
        ConfigVO config = configService.getByKey(configKey);
        return success(config);
    }

    @Operation(summary = "刷新所有配置缓存", description = "刷新所有配置缓存")
    @Log(businessType = BusinessType.UPDATE)
    @GetMapping("/refresh")
    public Result<?> refreshCache() {
        configService.refreshAllCache();
        return success();
    }

    @Operation(summary = "刷新配置缓存", description = "刷新配置缓存")
    @Log(businessType = BusinessType.UPDATE)
    @GetMapping("/refreshKeyCache/{key}")
    public Result<?> refreshKeyCache(@PathVariable("key") String configKey) {
        configService.refreshCache(configKey);
        return success();
    }

}
