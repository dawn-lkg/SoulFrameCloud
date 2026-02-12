package com.clm.modules.notice.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clm.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统通知实体类
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_notification")
public class Notification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型（1：系统通知，2：安全提醒，3：活动通知）
     */
    private Integer type;

    /**
     * 接收用户ID
     */
    private Long userId;

    /**
     * 是否已读（0：未读，1：已读）
     */
    @TableField(value = "`read`")
    private Boolean read;

    /**
     * 通知颜色（用于前端显示）
     */
    private String color;
}
