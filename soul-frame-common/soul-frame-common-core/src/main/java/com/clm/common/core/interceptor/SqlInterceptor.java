package com.clm.common.core.interceptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})}
)
@Slf4j
public class SqlInterceptor implements Interceptor {

    /**
     * 最小打印时间阈值（毫秒）
     */
    @Value("${mybatis-plus.sql-log.min-time:0}")
    private int minTime=0;

    /**
     * 慢SQL阈值（毫秒）
     */
    @Value("${mybatis-plus.sql-log.slow-sql-time:1000}")
    private int slowSqlTime=1000;

    /**
     * 超慢SQL阈值（毫秒）
     */
    @Value("${mybatis-plus.sql-log.very-slow-sql-time:5000}")
    private int verySlowSqlTime=5000;

    /**
     * 是否显示参数化前的原始SQL
     */
    @Value("${mybatis-plus.sql-log.show-original-sql:true}")
    private boolean showOriginalSql=true;

    /**
     * 是否打印返回结果
     */
    @Value("${mybatis-plus.sql-log.print-result:true}")
    private boolean printResult=true;

    /**
     * 是否打印参数
     */
    @Value("${mybatis-plus.sql-log.print-parameter:true}")
    private boolean printParameter=true;

    /**
     * 结果集最大打印数量
     */
    @Value("${mybatis-plus.sql-log.max-result-count:10}")
    private int maxResultCount=10;

    /**
     * 是否打印SQL日志
     */
    @Value("${mybatis-plus.sql-log.print-sql:true}")
    private boolean printSql = true;

    /**
     * SQL类型与操作名称的映射
     */
    private static final Map<SqlCommandType, String> SQL_TYPE_MAP = new EnumMap<>(SqlCommandType.class);

    /**
     * 排除的SQL ID列表
     */
    private Set<String> excludeSqlIds = new HashSet<>(){{
        add("com.clm.modules.system.mapper.OperLogMapper.insert");
        add("com.clm.modules.system.mapper.SysJobLogMapper.insert");
        add("com.clm.modules.system.mapper.PerformanceMetricsMapper.insert");
        add("com.clm.modules.system.mapper.LoginLogMapper.insert");
    }};

    /**
     * JSON配置
     */
    private static final JSONConfig jsonConfig = createJsonConfig();

    private static JSONConfig createJsonConfig() {
        JSONConfig config = new JSONConfig();
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        config.setIgnoreNullValue(false);
        return config;
    }

    static {
        SQL_TYPE_MAP.put(SqlCommandType.SELECT, "查询");
        SQL_TYPE_MAP.put(SqlCommandType.INSERT, "插入");
        SQL_TYPE_MAP.put(SqlCommandType.UPDATE, "更新");
        SQL_TYPE_MAP.put(SqlCommandType.DELETE, "删除");
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!printSql) {
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String sqlId = mappedStatement.getId();
        if (excludeSqlIds.contains(sqlId)) {
            return invocation.proceed();
        }
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        String rawSql = boundSql.getSql();
        String formattedRawSql = rawSql.replaceAll("\\s+", " ");

        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = invocation.proceed();
            return result;
        } finally {
            try {
                long costTime = System.currentTimeMillis() - startTime;
                if (costTime >= minTime) {
                    String sqlWithParams = formatSql(configuration, boundSql, formattedRawSql);
                    logSqlInfo(sqlCommandType, sqlId, sqlWithParams, formattedRawSql, costTime, parameter, result);
                }
            } catch (Exception e) {
                log.warn("SQL日志记录异常: {}", e.getMessage());
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        if (properties != null) {
            minTime = Integer.parseInt(properties.getProperty("minTime", "0"));
            slowSqlTime = Integer.parseInt(properties.getProperty("slowSqlTime", "1000"));
            verySlowSqlTime = Integer.parseInt(properties.getProperty("verySlowSqlTime", "5000"));
            showOriginalSql = Boolean.parseBoolean(properties.getProperty("showOriginalSql", "false"));
            printResult = Boolean.parseBoolean(properties.getProperty("printResult", "true"));
            printParameter = Boolean.parseBoolean(properties.getProperty("printParameter", "false"));
            maxResultCount = Integer.parseInt(properties.getProperty("maxResultCount", "10"));
            printSql = Boolean.parseBoolean(properties.getProperty("printSql", "true"));

            String excludeSqlIdsStr = properties.getProperty("excludeSqlIds");
            if (StrUtil.isNotBlank(excludeSqlIdsStr)) {
                excludeSqlIds = new HashSet<>(StrUtil.split(excludeSqlIdsStr, ','));
            }
        }
    }

    /**
     * 格式化SQL，替换占位符为实际参数
     */
    private String formatSql(Configuration configuration, BoundSql boundSql, String sql) {
        if (StrUtil.isBlank(sql)) {
            return "";
        }

        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if (parameterMappings.isEmpty() || parameterObject == null) {
            return sql;
        }

        try {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = replacePlaceholder(sql, parameterObject);
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = replacePlaceholder(sql, obj);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = replacePlaceholder(sql, obj);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("SQL格式化失败: {}", e.getMessage());
        }

        return sql;
    }

    private String replacePlaceholder(String sql, Object parameterObject) {
        String result;
        if (parameterObject == null) {
            result = "NULL";
        } else if (parameterObject instanceof String) {
            result = "'" + parameterObject.toString().replace("'", "''") + "'";
        } else if (parameterObject instanceof Date) {
            result = "'" + DateUtil.formatDateTime((Date) parameterObject) + "'";
        } else if (parameterObject instanceof byte[]) {
            result = "'[二进制数据]'";
        } else {
            result = parameterObject.toString();
        }
        return sql.replaceFirst("\\?", java.util.regex.Matcher.quoteReplacement(result));
    }

    /**
     * 记录SQL执行信息
     */
    private void logSqlInfo(SqlCommandType sqlCommandType, String sqlId, String sqlWithParams, String originalSql,
                            long costTime, Object parameter, Object result) {
        StringBuilder logBuilder = new StringBuilder();

        boolean isSlowSql = costTime >= slowSqlTime;
        boolean isVerySlowSql = costTime >= verySlowSqlTime;

        logBuilder.append("\n==============================SQL执行信息==============================\n");

        String sqlType = SQL_TYPE_MAP.getOrDefault(sqlCommandType, "未知");
        logBuilder.append("SQL操作: ").append(sqlType).append("\n");
        logBuilder.append("Mapper ID: ").append(sqlId).append("\n");

        logBuilder.append("执行耗时: ").append(costTime).append("ms");
        if (isVerySlowSql) {
            logBuilder.append(" [超慢SQL]");
        } else if (isSlowSql) {
            logBuilder.append(" [慢SQL]");
        }
        logBuilder.append("\n");

        if (showOriginalSql && !sqlWithParams.equals(originalSql)) {
            logBuilder.append("原始SQL: ").append(originalSql).append("\n");
        }

        logBuilder.append("实际SQL: ").append(sqlWithParams).append("\n");

        if(printParameter){
            printParameters(logBuilder, parameter);
        }

        // 打印结果
        if (result != null) {
            // 查询操作
            if (sqlCommandType == SqlCommandType.SELECT) {
                if (result instanceof Collection<?> collection) {
                    logBuilder.append("返回行数: ").append(collection.size()).append("\n");

                    // 打印结果集
                    if (printResult && !collection.isEmpty()) {
                        try {
                            if (collection.size() <= maxResultCount) {
                                logBuilder.append("查询结果: ").append(safeToJson(collection)).append("\n");
                            } else {
                                List<?> subList = new ArrayList<>(collection);
                                if (subList.size() > maxResultCount) {
                                    subList = subList.subList(0, maxResultCount);
                                }
                                logBuilder.append("查询结果(前").append(maxResultCount).append("条): ")
                                        .append(safeToJson(subList)).append("\n");
                            }
                        } catch (Exception e) {
                            logBuilder.append("查询结果: [无法序列化的结果]").append("\n");
                        }
                    }
                } else if (result instanceof Map<?, ?> map) {
                    logBuilder.append("返回行数: ").append(map.size()).append("\n");

                    // 打印Map结果
                    if (printResult) {
                        try {
                            logBuilder.append("查询结果: ").append(safeToJson(map)).append("\n");
                        } catch (Exception e) {
                            logBuilder.append("查询结果: [无法序列化的结果]").append("\n");
                        }
                    }
                } else {
                    logBuilder.append("返回行数: 1").append("\n");

                    if (printResult) {
                        try {
                            logBuilder.append("查询结果: ").append(safeToJson(result)).append("\n");
                        } catch (Exception e) {
                            logBuilder.append("查询结果: [无法序列化的结果]").append("\n");
                        }
                    }
                }
            } else { // 非查询操作
                logBuilder.append("影响行数: ").append(result).append("\n");
            }
        } else {
            logBuilder.append("执行结果: null").append("\n");
        }

        logBuilder.append("===================================================================");

        if (isVerySlowSql) {
            log.warn(logBuilder.toString());
        } else if (isSlowSql) {
            log.warn(logBuilder.toString());
        } else {
            log.info(logBuilder.toString());
        }
    }

    /**
     * 打印参数详情
     */
    private void printParameters(StringBuilder logBuilder, Object parameter) {
        if (parameter == null) {
            logBuilder.append("参数: 无\n");
            return;
        }

        try {
            // 1. 如果参数是简单类型
            if (parameter instanceof String ||
                    parameter instanceof Number ||
                    parameter instanceof Boolean ||
                    parameter instanceof Date) {
                logBuilder.append("参数值: ").append(formatParameterValue(parameter)).append("\n");
            }
            // 2. 如果参数是Map
            else if (parameter instanceof Map<?, ?> paramMap) {

                // 过滤掉MyBatis自动生成的参数名（param1, param2, ...）
                Map<Object, Object> filteredParams = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : paramMap.entrySet()) {
                    Object key = entry.getKey();
                    // 跳过自动生成的参数名
                    if (!(key instanceof String) || !((String) key).matches("param\\d+")) {
                        filteredParams.put(key, entry.getValue());
                    }
                }

                // 如果过滤后没有参数了，可能是所有参数都是自动生成的
                // 在这种情况下保留原始参数
                if (filteredParams.isEmpty() && !paramMap.isEmpty()) {
                    filteredParams = new LinkedHashMap<>(paramMap);
                }

                logBuilder.append("参数列表:\n");
                for (Map.Entry<Object, Object> entry : filteredParams.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();

                    logBuilder.append("  ").append(key).append(" = ");

                    // 特殊处理MyBatis-Plus的查询条件包装器
                    if (isMyBatisPlusWrapper(value)) {
                        logBuilder.append(extractWrapperInfo(value));
                    } else {
                        logBuilder.append(formatParameterValue(value));
                    }
                    logBuilder.append("\n");
                }
            }
            // 3. 如果参数是MyBatis-Plus的查询条件包装器
            else if (isMyBatisPlusWrapper(parameter)) {
                logBuilder.append("参数对象: ").append(extractWrapperInfo(parameter)).append("\n");
            }
            // 4. 使用JSON序列化其他复杂对象
            else {
                String paramStr = safeToJson(parameter);
                logBuilder.append("参数对象: ").append(paramStr).append("\n");
            }
        } catch (Exception e) {
            logBuilder.append("参数解析异常: ").append(e.getMessage()).append("\n");
        }
    }

    /**
     * 检查对象是否为MyBatis-Plus的查询条件包装器
     */
    private boolean isMyBatisPlusWrapper(Object obj) {
        if (obj == null) {
            return false;
        }

        String className = obj.getClass().getName();
        return className.startsWith("com.baomidou.mybatisplus.core.conditions");
    }

    /**
     * 提取MyBatis-Plus查询条件包装器中的信息
     */
    private String extractWrapperInfo(Object wrapper) {
        if (wrapper == null) {
            return "null";
        }

        try {
            // 获取参数映射
            Map<String, Object> paramValueMappings = new HashMap<>();
            try {
                // 尝试获取paramNameValuePairs字段
                Field paramNameValuePairsField = getField(wrapper.getClass(), "paramNameValuePairs");
                if (paramNameValuePairsField != null) {
                    paramNameValuePairsField.setAccessible(true);
                    Object paramNameValuePairs = paramNameValuePairsField.get(wrapper);
                    if (paramNameValuePairs instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> pairMap = (Map<String, Object>) paramNameValuePairs;
                        paramValueMappings.putAll(pairMap);
                    }
                }
            } catch (Exception e) {
                // 忽略获取参数映射的错误
            }

            // 尝试调用wrapper.getCustomSqlSegment()方法获取SQL片段
            String sqlSegment = null;
            try {
                Method getCustomSqlSegment = wrapper.getClass().getMethod("getCustomSqlSegment");
                Object result = getCustomSqlSegment.invoke(wrapper);
                if (result != null) {
                    sqlSegment = result.toString();
                }
            } catch (Exception e) {
                // 忽略并尝试下一个方法
            }

            // 如果getCustomSqlSegment失败，尝试调用wrapper.getSqlSegment()
            if (sqlSegment == null) {
                try {
                    Method getSqlSegment = wrapper.getClass().getMethod("getSqlSegment");
                    Object result = getSqlSegment.invoke(wrapper);
                    if (result != null) {
                        sqlSegment = result.toString();
                    }
                } catch (Exception e) {
                    // 忽略
                }
            }

            // 如果获取到了SQL片段，替换占位符
            if (sqlSegment != null && !sqlSegment.isEmpty()) {
                // 替换#{ew.paramNameValuePairs.XXX}形式的占位符
                for (Map.Entry<String, Object> entry : paramValueMappings.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    String placeholder = "#{ew.paramNameValuePairs." + key + "}";
                    String formattedValue = formatParameterValue(value);

                    sqlSegment = sqlSegment.replace(placeholder, formattedValue);
                }

                return "条件: " + sqlSegment;
            }

            // 如果以上方法都失败，尝试获取实体条件
            Object entity = null;
            try {
                Field entityField = getField(wrapper.getClass(), "entity");
                if (entityField != null) {
                    entityField.setAccessible(true);
                    entity = entityField.get(wrapper);
                }
            } catch (Exception e) {
                // 忽略
            }

            if (entity != null) {
                return "实体条件: " + safeToJson(entity) + ", 参数值: " + safeToJson(paramValueMappings);
            }

            if (!paramValueMappings.isEmpty()) {
                return "参数映射: " + safeToJson(paramValueMappings);
            }

            return wrapper.getClass().getSimpleName() + "@" + Integer.toHexString(wrapper.hashCode());

        } catch (Exception e) {
            return wrapper.getClass().getSimpleName() + "(解析失败: " + e.getMessage() + ")";
        }
    }

    /**
     * 格式化参数值
     */
    private String formatParameterValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "'" + value + "'";
        } else if (value instanceof Date) {
            return "'" + DateUtil.formatDateTime((Date) value) + "'";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            // 对于复杂对象，尝试JSON序列化
            try {
                return safeToJson(value);
            } catch (Exception e) {
                return value.toString();
            }
        }
    }

    /**
     * 递归查找类中的字段（包括父类）
     */
    private Field getField(Class<?> clazz, String fieldName) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }

        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // 在父类中查找
            return getField(clazz.getSuperclass(), fieldName);
        }
    }

    /**
     * 安全地将对象转换为JSON字符串
     */
    private String safeToJson(Object obj) {
        try {
            return JSONUtil.toJsonStr(obj, jsonConfig);
        } catch (Exception e) {
            try {
                return JSONUtil.toJsonStr(obj);
            } catch (Exception ex) {
                return "[无法序列化的对象]";
            }
        }
    }
}