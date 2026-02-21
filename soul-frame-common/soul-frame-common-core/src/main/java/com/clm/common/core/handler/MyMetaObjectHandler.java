package com.clm.common.core.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus元数据处理器
 * 用于自动填充创建时间、更新时间、创建者、更新者等字段
 * 
 * @author 陈黎明
 * @date 2025/3/2
 */
@Slf4j
@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (metaObject.hasSetter("createTime")) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            }

            if (metaObject.hasSetter("updateTime")) {
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }

            if (metaObject.hasSetter("createBy")) {
                String username = "未登录";
                if (StpUtil.isLogin()) {
                    username = StpUtil.getLoginIdAsString();
                }
                this.strictInsertFill(metaObject, "createBy", String.class, username);
            }

            if (metaObject.hasSetter("updateBy")) {
                String username = "未登录";
                if (StpUtil.isLogin()) {
                    username = StpUtil.getLoginIdAsString();
                }
                this.strictInsertFill(metaObject, "updateBy", String.class, username);
            }

            if (metaObject.hasSetter("delFlag")) {
                this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
            }
        } catch (Exception e) {
            log.error("自动填充异常", e);
        }
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            // 更新时间
            if (metaObject.hasSetter("updateTime")) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
            
            // 更新者
            if (metaObject.hasSetter("updateBy")) {
                // 获取当前登录用户
                String username = "未登录";
                if (StpUtil.isLogin()) {
                    username = StpUtil.getLoginIdAsString();
                }
                this.strictUpdateFill(metaObject, "updateBy", String.class, username);
            }
        } catch (Exception e) {
            log.error("自动填充异常", e);
        }
    }
} 