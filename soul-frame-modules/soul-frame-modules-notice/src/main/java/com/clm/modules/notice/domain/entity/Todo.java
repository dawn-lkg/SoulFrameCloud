package com.clm.modules.notice.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clm.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 待办事项实体类
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_todo")
public class Todo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 待办ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 待办标题
     */
    private String title;

    /**
     * 待办内容
     */
    private String content;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 状态（pending：待办，completed：已完成，overdue：已逾期）
     */
    private String status;

    /**
     * 优先级（high：高，medium：中，low：低）
     */
    private String priority;
}
