package com.clm.common.redis.constants;

/**
 * Redis键常量
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
public class RedisKeyConstants {

    /**
     * 系统相关
     */
    public static class System {
        /** 系统配置前缀 */
        public static final String CONFIG_PREFIX = "sys:config:";
        /** 字典数据前缀 */
        public static final String DICT_PREFIX = "sys:dict:";
        /** 部门数据前缀 */
        public static final String DEPT_PREFIX = "sys:dept:";
        /** 用户数据前缀 */
        public static final String USER_PREFIX = "sys:user:";
        /** 角色数据前缀 */
        public static final String ROLE_PREFIX = "sys:role:";
        /** 菜单数据前缀 */
        public static final String MENU_PREFIX = "sys:menu:";
        /** 验证码数据前缀 */
        public static final String CAPTCHA_PREFIX = "sys:captcha:";
        /**
         * 系统配置数据前缀
         */
        public static final String SYSTEM_CONFIG_PREFIX = "sys:config:";
        /**
         * 系统配置数据过期时间
         */
        public static final long SYSTEM_CONFIG_TIMEOUT = 60 * 60 * 24 * 7;
        /**
         * 字典数据过期时间
         */
        public static final long DICT_TIMEOUT = 60 * 60 * 24 * 7;
    }

    /**
     * 认证相关
     */
    public static class Auth {
        /** 登录用户Token前缀 */
        public static final String LOGIN_TOKEN_PREFIX = "auth:token:";
        /** 在线用户前缀 */
        public static final String ONLINE_USER_PREFIX = "auth:online:";
        /** 用户权限前缀 */
        public static final String USER_PERMS_PREFIX = "auth:perms:";
        /** 用户角色前缀 */
        public static final String USER_ROLES_PREFIX = "auth:roles:";
    }

    /**
     * 业务相关
     */
    public static class Business {
        /** 通用业务缓存前缀 */
        public static final String CACHE_PREFIX = "biz:cache:";
        /** 业务锁前缀 */
        public static final String LOCK_PREFIX = "biz:lock:";
        /** 限流前缀 */
        public static final String RATE_LIMIT_PREFIX = "biz:rate:";
        /** 布隆过滤器前缀 */
        public static final String BLOOM_FILTER_PREFIX = "biz:bloom:";
    }

    /**
     * 监控相关
     */
    public static class Monitor {
        /** 在线用户数量 */
        public static final String ONLINE_COUNT = "monitor:online:count";
        /** 系统信息 */
        public static final String SYSTEM_INFO = "monitor:system:info";
        /** JVM信息 */
        public static final String JVM_INFO = "monitor:jvm:info";
        /** 访问统计 */
        public static final String ACCESS_STATS = "monitor:access:stats";
        /**
         * CPU使用率
         */
        public static final String CPU_USAGE = "monitor:cpu:usage";
        /**
         * 内存使用率
         */
        public static final String MEM_USAGE = "monitor:mem:usage";
    }

    /**
     * 消息相关
     */
    public static class Message {
        /** 消息通知前缀 */
        public static final String NOTICE_PREFIX = "msg:notice:";
        /** 系统公告前缀 */
        public static final String ANNOUNCEMENT_PREFIX = "msg:announcement:";
        /** 用户消息前缀 */
        public static final String USER_MSG_PREFIX = "msg:user:";
    }

    /**
     * 定时任务相关
     */
    public static class Job {
        /** 任务锁前缀 */
        public static final String LOCK_PREFIX = "job:lock:";
        /** 任务状态前缀 */
        public static final String STATUS_PREFIX = "job:status:";
        /** 任务执行记录前缀 */
        public static final String LOG_PREFIX = "job:log:";
    }

    /**
     * 工具相关
     */
    public static class Tool {
        /** 代码生成配置前缀 */
        public static final String GEN_CONFIG_PREFIX = "tool:gen:config:";
        /** 表信息前缀 */
        public static final String TABLE_INFO_PREFIX = "tool:gen:table:";
    }

    /**
     * 获取完整的key
     *
     * @param prefix 前缀
     * @param key 键
     * @return 完整的key
     */
    public static String getFullKey(String prefix, String key) {
        return prefix + key;
    }
} 