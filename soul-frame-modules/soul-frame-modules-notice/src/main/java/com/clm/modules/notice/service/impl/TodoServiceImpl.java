package com.clm.modules.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.exception.BaseException;
import com.clm.modules.system.domain.dto.TodoDTO;
import com.clm.modules.system.domain.entity.Todo;
import com.clm.modules.system.domain.param.TodoQueryParam;
import com.clm.modules.system.domain.vo.TodoVO;
import com.clm.modules.system.enums.TodoStatusEnum;
import com.clm.modules.system.mapper.TodoMapper;
import com.clm.modules.system.service.NotificationPushService;
import com.clm.modules.system.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 待办事项服务实现类
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Service
@RequiredArgsConstructor
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    private final NotificationPushService notificationPushService;

    /**
     * 获取当前用户的待办事项分页列表
     *
     * @param param 查询参数
     * @return 待办事项分页列表
     */
    @Override
    public IPage<TodoVO> getTodoPage(TodoQueryParam param) {
        // 更新已过期的待办事项状态
        updateOverdueTodos();

        Long userId = LoginHelper.getUserId();
        Page<Todo> page = new Page<>(param.getPageNum(), param.getPageSize());
        return baseMapper.selectTodoPage(page, userId, param.getTitle(), param.getStatus(), param.getPriority());
    }

    /**
     * 获取待办事项详情
     *
     * @param id 待办事项ID
     * @return 待办事项详情
     */
    @Override
    public TodoVO getTodoDetail(Long id) {
        Long userId = LoginHelper.getUserId();

        // 检查待办事项是否存在且属于当前用户
        Todo todo = getById(id);
        if (todo == null) {
            throw new BaseException("待办事项不存在");
        }
        if (!todo.getUserId().equals(userId)) {
            throw new BaseException("无权查看此待办事项");
        }

        TodoVO todoVO = new TodoVO();
        BeanUtils.copyProperties(todo, todoVO);

        return todoVO;
    }

    /**
     * 创建待办事项
     *
     * @param todoDTO 待办事项数据
     * @return 创建的待办事项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoVO createTodo(TodoDTO todoDTO) {
        Long userId = LoginHelper.getUserId();

        Todo todo = new Todo();
        BeanUtils.copyProperties(todoDTO, todo);

        todo.setUserId(userId)
                .setStatus("pending");

        // 如果截止时间已过，则设置状态为已逾期
        if (todo.getDeadline() != null && todo.getDeadline().isBefore(LocalDateTime.now())) {
            todo.setStatus("overdue");
        }

        save(todo);

        TodoVO todoVO = new TodoVO();
        BeanUtils.copyProperties(todo, todoVO);

        // 推送待办事项
        notificationPushService.pushTodo(userId, todoVO);

        return todoVO;
    }

    /**
     * 更新待办事项
     *
     * @param id      待办事项ID
     * @param todoDTO 待办事项数据
     * @return 更新后的待办事项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoVO updateTodo(Long id, TodoDTO todoDTO) {
        Long userId = LoginHelper.getUserId();

        // 检查待办事项是否存在且属于当前用户
        Todo todo = getById(id);
        if (todo == null) {
            throw new BaseException("待办事项不存在");
        }
        if (!todo.getUserId().equals(userId)) {
            throw new BaseException("无权操作此待办事项");
        }

        // 如果待办事项已完成，不允许修改
        if ("completed".equals(todo.getStatus())) {
            throw new BaseException("已完成的待办事项不能修改");
        }

        BeanUtils.copyProperties(todoDTO, todo);
        todo.setId(id)
                .setUpdateTime(LocalDateTime.now());

        // 如果截止时间已过，则设置状态为已逾期
        if (todo.getDeadline() != null && todo.getDeadline().isBefore(LocalDateTime.now())) {
            todo.setStatus("overdue");
        } else {
            todo.setStatus("pending");
        }

        updateById(todo);

        TodoVO todoVO = new TodoVO();
        BeanUtils.copyProperties(todo, todoVO);

        return todoVO;
    }

    /**
     * 删除待办事项
     *
     * @param id 待办事项ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTodo(Long id) {
        Long userId = LoginHelper.getUserId();

        // 检查待办事项是否存在且属于当前用户
        Todo todo = getById(id);
        if (todo == null) {
            throw new BaseException("待办事项不存在");
        }
        if (!todo.getUserId().equals(userId)) {
            throw new BaseException("无权操作此待办事项");
        }

        // 逻辑删除
        LambdaUpdateWrapper<Todo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Todo::getId, id)
                .eq(Todo::getUserId, userId)
                .set(Todo::getDelFlag, 1)
                .set(Todo::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    /**
     * 标记待办事项为已完成
     *
     * @param id 待办事项ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTodo(Long id) {
        Long userId = LoginHelper.getUserId();

        // 检查待办事项是否存在且属于当前用户
        Todo todo = getById(id);
        if (todo == null) {
            throw new BaseException("待办事项不存在");
        }
        if (!todo.getUserId().equals(userId)) {
            throw new BaseException("无权操作此待办事项");
        }

        // 如果已经是已完成状态，直接返回
        if (TodoStatusEnum.COMPLETED.getCode().equals(todo.getStatus())) {
            return;
        }

        // 更新为已完成
        LambdaUpdateWrapper<Todo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Todo::getId, id)
                .eq(Todo::getUserId, userId)
                .set(Todo::getStatus, TodoStatusEnum.COMPLETED.getCode())
                .set(Todo::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);

        // 推送待办事项
        notificationPushService.pushTodo(userId, new Object());
    }

    @Override
    public void incompleteTodo(Long id) {
        Long userId = LoginHelper.getUserId();

        // 检查待办事项是否存在且属于当前用户
        Todo todo = getById(id);
        if (todo == null) {
            throw new BaseException("待办事项不存在");
        }
        if (!todo.getUserId().equals(userId)) {
            throw new BaseException("无权操作此待办事项");
        }

        // 如果已经是已完成状态，直接返回
        if (TodoStatusEnum.PENDING.getCode().equals(todo.getStatus())) {
            return;
        }

        // 更新为已完成
        LambdaUpdateWrapper<Todo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Todo::getId, id)
                .eq(Todo::getUserId, userId)
                .set(Todo::getStatus, TodoStatusEnum.PENDING.getCode())
                .set(Todo::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);

        // 推送待办事项
        notificationPushService.pushTodo(userId, new Object());

    }

    /**
     * 获取当前用户的待处理待办事项数量
     *
     * @return 待处理待办事项数量
     */
    @Override
    public Integer getPendingCount() {
        Long userId = LoginHelper.getUserId();
        return baseMapper.selectPendingCount(userId);
    }

    /**
     * 更新已过期的待办事项状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOverdueTodos() {
        baseMapper.updateOverdueTodos();
    }
}
