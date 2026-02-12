package com.clm.modules.notice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.dto.TodoDTO;
import com.clm.modules.system.domain.entity.Todo;
import com.clm.modules.system.domain.param.TodoQueryParam;
import com.clm.modules.system.domain.vo.TodoVO;

/**
 * 待办事项服务接口
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
public interface TodoService extends IService<Todo> {

    /**
     * 获取当前用户的待办事项分页列表
     *
     * @param param 查询参数
     * @return 待办事项分页列表
     */
    IPage<TodoVO> getTodoPage(TodoQueryParam param);

    /**
     * 获取待办事项详情
     *
     * @param id 待办事项ID
     * @return 待办事项详情
     */
    TodoVO getTodoDetail(Long id);

    /**
     * 创建待办事项
     *
     * @param todoDTO 待办事项数据
     * @return 创建的待办事项
     */
    TodoVO createTodo(TodoDTO todoDTO);

    /**
     * 更新待办事项
     *
     * @param id      待办事项ID
     * @param todoDTO 待办事项数据
     * @return 更新后的待办事项
     */
    TodoVO updateTodo(Long id, TodoDTO todoDTO);

    /**
     * 删除待办事项
     *
     * @param id 待办事项ID
     */
    void deleteTodo(Long id);

    /**
     * 标记待办事项为已完成
     *
     * @param id 待办事项ID
     */
    void completeTodo(Long id);

    /**
     * 撤销待办事项为未完成
     *
     * @param id 待办事项ID
     */
    void incompleteTodo(Long id);

    /**
     * 获取当前用户的待处理待办事项数量
     *
     * @return 待处理待办事项数量
     */
    Integer getPendingCount();

    /**
     * 更新已过期的待办事项状态
     */
    void updateOverdueTodos();

}
