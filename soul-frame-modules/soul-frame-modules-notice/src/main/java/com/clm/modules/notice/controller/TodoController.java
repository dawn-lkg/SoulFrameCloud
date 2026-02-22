package com.clm.modules.notice.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.annotation.Log;
import com.clm.modules.notice.domain.dto.TodoDTO;
import com.clm.modules.notice.domain.param.TodoQueryParam;
import com.clm.modules.notice.domain.vo.TodoVO;
import com.clm.modules.notice.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 待办事项控制器
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Tag(name = "待办事项管理")
@RestController
@RequestMapping("/notice/todo")
@RequiredArgsConstructor
@SaCheckLogin
public class TodoController extends BaseController {

    private final TodoService todoService;

    @Operation(summary = "获取待办事项列表")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/list")
    public Result<IPage<TodoVO>> list(@Valid TodoQueryParam param) {
        return success(todoService.getTodoPage(param));
    }

    @Operation(summary = "获取待办事项详情")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/{id}")
    public Result<TodoVO> detail(@Parameter(description = "待办事项ID") @PathVariable Long id) {
        return success(todoService.getTodoDetail(id));
    }

    @Operation(summary = "创建待办事项")
    @Log(businessType = BusinessType.INSERT)
    @PostMapping
    public Result<TodoVO> create(@RequestBody @Valid TodoDTO todoDTO) {
        return success(todoService.createTodo(todoDTO));
    }

    @Operation(summary = "更新待办事项")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public Result<TodoVO> update(@Parameter(description = "待办事项ID") @PathVariable Long id,
                                 @RequestBody TodoDTO todoDTO) {
        return success(todoService.updateTodo(id, todoDTO));
    }

    @Operation(summary = "删除待办事项")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<?> delete(@Parameter(description = "待办事项ID") @PathVariable Long id) {
        todoService.deleteTodo(id);
        return success();
    }

    @Operation(summary = "标记待办事项为已完成")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{id}")
    public Result<?> complete(@Parameter(description = "待办事项ID") @PathVariable Long id) {
        todoService.completeTodo(id);
        return success();
    }

    @Operation(summary = "更新待办事项的为未完成")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/incomplete/{id}")
    public Result<?> incomplete(@Parameter(description = "待办事项ID") @PathVariable Long id) {
        todoService.incompleteTodo(id);
        return success();
    }

    @Operation(summary = "获取待处理的待办事项数量")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/pending/count")
    public Result<Integer> pendingCount() {
        return success(todoService.getPendingCount());
    }

}
