-- =============================================
-- Seata 分布式事务测试 - 数据库初始化脚本
-- 场景：订单服务 + 库存服务 分布式事务
-- =============================================

-- 创建订单数据库
CREATE DATABASE IF NOT EXISTS order_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE order_db;

-- 订单表
CREATE TABLE IF NOT EXISTS `t_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_name` VARCHAR(255) NOT NULL COMMENT '商品名称',
    `count` INT NOT NULL COMMENT '购买数量',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `status` TINYINT NOT NULL DEFAULT '0' COMMENT '订单状态 0-创建 1-已支付 2-已取消',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- Seata undo_log 表（必须）
CREATE TABLE IF NOT EXISTS `undo_log` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `branch_id` bigint NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) NOT NULL,
    `rollback_info` longblob NOT NULL,
    `log_status` int NOT NULL,
    `log_created` datetime NOT NULL,
    `log_modified` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Seata undo log表';

-- =============================================
-- 创建库存数据库
-- =============================================
CREATE DATABASE IF NOT EXISTS stock_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE stock_db;

-- 库存表
CREATE TABLE IF NOT EXISTS `t_stock` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    `product_name` VARCHAR(255) NOT NULL COMMENT '商品名称',
    `stock` INT NOT NULL DEFAULT '0' COMMENT '库存数量',
    `reserved_stock` INT NOT NULL DEFAULT '0' COMMENT '预留库存',
    `version` INT NOT NULL DEFAULT '0' COMMENT '版本号（乐观锁）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_name` (`product_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 初始化测试数据
INSERT INTO `t_stock` (`product_name`, `stock`, `reserved_stock`) VALUES
('iPhone 15 Pro', 100, 0),
('MacBook Pro', 50, 0),
('iPad Pro', 80, 0);

-- Seata undo_log 表（必须）
CREATE TABLE IF NOT EXISTS `undo_log` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `branch_id` bigint NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) NOT NULL,
    `rollback_info` longblob NOT NULL,
    `log_status` int NOT NULL,
    `log_created` datetime NOT NULL,
    `log_modified` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Seata undo log表';

-- =============================================
-- 测试用例说明
-- =============================================
-- 正常场景：创建订单 + 扣减库存（成功）
-- 异常场景：创建订单 + 扣减库存（库存不足时整体回滚）
