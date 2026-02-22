package com.clm.modules.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.modules.notice.domain.entity.Todo;
import com.clm.modules.notice.domain.vo.TodoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 待办事项数据库访问层
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {

    /**
     * 获取当前用户的待办事项分页列表
     *
     * @param page     分页参数
     * @param userId   用户ID
     * @param title    待办标题
     * @param status   状态
     * @param priority 优先级
     * @return 待办事项分页列表
     */
    IPage<TodoVO> selectTodoPage(Page<Todo> page,
                                 @Param("userId") Long userId,
                                 @Param("title") String title,
                                 @Param("status") String status,
                                 @Param("priority") String priority);

    /**
     * 获取当前用户的待处理待办事项数量
     *
     * @param userId 用户ID
     * @return 待处理待办事项数量
     */
    Integer selectPendingCount(@Param("userId") Long userId);

    /**
     * 更新已过期的待办事项状态
     */
    void updateOverdueTodos();
}
