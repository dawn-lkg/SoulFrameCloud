package com.clm.common.redis.utils;

import com.clm.common.core.utils.ObjectConvertUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Redis工具类
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
@Component
@SuppressWarnings("all")
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private ObjectMapper objectMapper;
    
    @Resource
    private RedisSerializer<Object> compatibleJackson2JsonRedisSerializer;

    // ============================== String类型操作 ==============================

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取缓存并转换为指定类型
     *
     * @param key 键
     * @param clazz 类型
     * @param <T> 数据类型
     * @return 值
     */
    public <T> T get(String key, Class<T> clazz) {
        if (key == null) {
            return null;
        }
        
        // 尝试常规方式获取
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        
        // 如果类型匹配，直接返回
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        
        try {
            // 尝试使用Jackson转换
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            // 如果转换失败，尝试直接从Redis获取原始数据并使用兼容序列化器
            try {
                byte[] rawValue = redisTemplate.getConnectionFactory().getConnection()
                        .stringCommands().get(key.getBytes());
                if (rawValue != null) {
                    return (T) compatibleJackson2JsonRedisSerializer.deserialize(rawValue);
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("缓存值类型不匹配: " + ex.getMessage(), ex);
            }
        }
        
        throw new IllegalArgumentException("缓存值类型不匹配");
    }

    /**
     * 从缓存中获取数据，如果不存在则通过回调函数获取并缓存
     *
     * @param key 缓存键
     * @param callback 回调函数，用于在缓存不存在时获取数据
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 缓存数据
     * @param <T> 数据类型
     */
    public <T> T get(String key, CacheCallback<T> callback, long timeout, TimeUnit unit) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object value = ops.get(key);
        if (value != null) {
            return (T) value;
        }

        T newValue = callback.get();
        if (newValue != null) {
            ops.set(key, newValue, timeout, unit);
        }
        return newValue;
    }

    /**
     * 从缓存中获取数据
     *
     * @param prefix   前缀
     * @param key      缓存键
     * @param callback 回调函数
     * @param timeout  缓存过期时间
     * @param unit     时间单位
     * @return 缓存数据
     */
    public <T> T get(String prefix, String key, CacheCallback<T> callback, long timeout, TimeUnit unit) {
        return get(prefix + key, callback, timeout, unit);
    }

    /**
     * 从缓存中获取数据（默认24小时过期）
     *
     * @param key 缓存键
     * @param callback 回调函数
     * @return 缓存数据
     * @param <T> 数据类型
     */
    public <T> T get(String key, CacheCallback<T> callback) {
        return get(key, callback, 24, TimeUnit.HOURS);
    }

    /**
     * 设置缓存
     *
     * @param key 键
     * @param value 值
     * @param timeout 时间
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置缓存（默认24小时）
     *
     * @param key 键
     * @param value 值
     */
    public void set(String key, Object value) {
        set(key, value, 24, TimeUnit.HOURS);
    }

    /**
     * 递增
     *
     * @param key 键
     * @param delta 递增因子
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key 键
     * @param delta 递减因子
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ============================== Hash类型操作 ==============================

    /**
     * 获取Hash中的数据
     *
     * @param key 键
     * @param item 项
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取Hash中的所有数据
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置Hash数据
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 设置Hash数据并设置时间
     *
     * @param key 键
     * @param map 对应多个键值
     * @param timeout 时间
     * @param unit 时间单位
     */
    public void hmset(String key, Map<String, Object> map, long timeout, TimeUnit unit) {
        redisTemplate.opsForHash().putAll(key, map);
        expire(key, timeout, unit);
    }

    /**
     * 向Hash中放入数据
     *
     * @param key 键
     * @param item 项
     * @param value 值
     */
    public void hset(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    // ============================== 公共方法 ==============================

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 键集合
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     *
     * @param key 键
     * @param timeout 时间
     * @param unit 时间单位
     * @return true成功 false失败
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 时间(秒) 返回0代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 获取匹配模式的所有key
     *
     * @param pattern 模式
     * @return key集合
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 缓存回调接口
     *
     * @param <T> 数据类型
     */
    @FunctionalInterface
    public interface CacheCallback<T> {
        /**
         * 获取数据
         *
         * @return 数据
         */
        T get();
    }

    // ============================== List类型操作 ==============================

    /**
     * 获取List缓存的内容
     *
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置 0到-1代表所有值
     * @return List
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取List缓存的内容
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置 0到-1代表所有值
     * @return List
     */
    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        return ObjectConvertUtils.convertList(lRange(key, start, end), clazz);
    }

    /**
     * 获取List缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 通过索引获取List中的值
     *
     * @param key 键
     * @param index 索引 index>=0时，0表头，1第二个元素，依次类推；index<0时，-1表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将值放入List缓存
     *
     * @param key 键
     * @param value 值
     */
    public void lPush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将值放入List缓存并设置时间
     *
     * @param key 键
     * @param value 值
     * @param timeout 时间
     * @param unit 时间单位
     */
    public void lPush(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForList().rightPush(key, value);
        expire(key, timeout, unit);
    }

    /**
     * 将List放入缓存
     *
     * @param key 键
     * @param values 值
     */
    public void lPushAll(String key, List<Object> values) {
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 将List放入缓存并设置时间
     *
     * @param key 键
     * @param values 值
     * @param timeout 时间
     * @param unit 时间单位
     */
    public void lPushAll(String key, List<Object> values, long timeout, TimeUnit unit) {
        redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, timeout, unit);
    }

    /**
     * 从List缓存中移除N个值为value的元素
     *
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    // ============================== Set类型操作 ==============================

    /**
     * 获取Set中的所有值
     *
     * @param key 键
     * @return Set
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断Set中是否存在value
     *
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sIsMember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    /**
     * 将数据放入Set缓存
     *
     * @param key 键
     * @param values 值
     * @return 成功个数
     */
    public long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 将Set放入缓存并设置时间
     *
     * @param key 键
     * @param timeout 时间
     * @param unit 时间单位
     * @param values 值
     * @return 成功个数
     */
    public long sAdd(String key, long timeout, TimeUnit unit, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, timeout, unit);
        return count;
    }

    /**
     * 获取Set缓存的长度
     *
     * @param key 键
     * @return 长度
     */
    public long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除Set中的值
     *
     * @param key 键
     * @param values 值
     * @return 移除的个数
     */
    public long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    // ============================== ZSet类型操作 ==============================

    /**
     * 添加元素到ZSet
     *
     * @param key 键
     * @param value 值
     * @param score 分数
     * @return true成功 false失败
     */
    public boolean zAdd(String key, Object value, double score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
    }

    /**
     * 获取ZSet中指定范围的元素
     *
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置 -1代表所有
     * @return Set
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取ZSet中指定分数范围的元素
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return Set
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取ZSet中元素的分数
     *
     * @param key 键
     * @param value 值
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 移除ZSet中的元素
     *
     * @param key 键
     * @param values 值
     * @return 移除的个数
     */
    public long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    // ============================== 分布式锁 ==============================

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey 锁键
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
            .setIfAbsent(lockKey, requestId, expireTime, unit));
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey 锁键
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = redisTemplate.execute((RedisCallback<Long>) connection ->
            connection.scriptingCommands().eval(
                script.getBytes(),
                ReturnType.INTEGER,
                1,
                lockKey.getBytes(),
                requestId.getBytes()
            )
        );
        return Long.valueOf(1).equals(result);
    }

    // ============================== 消息发布订阅 ==============================

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息
     */
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

    // ============================== 批量操作 ==============================

    /**
     * 批量获取
     *
     * @param keys 键集合
     * @return 值集合
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量设置
     *
     * @param map 键值对
     */
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 批量删除
     *
     * @param pattern 模式
     */
    public void deletePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ============================== 事务支持 ==============================

    /**
     * 在事务内执行操作
     *
     * @param action 操作
     * @return 执行结果列表
     */
    public List<Object> executeTransaction(Consumer<RedisOperations<String, Object>> action) {
        return redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) {
                operations.multi();
                action.accept(operations);
                return operations.exec();
            }
        });
    }

    /**
     * 监视key
     *
     * @param keys 键
     */
    public void watch(String... keys) {
        redisTemplate.watch(Arrays.asList(keys));
    }

    /**
     * 取消监视所有key
     */
    public void unwatch() {
        redisTemplate.unwatch();
    }

    // ============================== 管道操作 ==============================

    /**
     * 在管道中执行操作
     *
     * @param action 操作
     * @return 执行结果列表
     */
    public List<Object> executePipelined(Consumer<RedisOperations<String, Object>> action) {
        return redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) {
                action.accept(operations);
                return null;
            }
        });
    }

    // ============================== 布隆过滤器 ==============================

    /**
     * 创建布隆过滤器
     *
     * @param key 过滤器键
     * @param expectedInsertions 预期插入量
     * @param fpp 误判率
     */
    public void createBloomFilter(String key, long expectedInsertions, double fpp) {
        String script = "return redis.call('BF.RESERVE', KEYS[1], ARGV[1], ARGV[2])";
        redisTemplate.execute((RedisCallback<Boolean>) connection ->
            connection.scriptingCommands().eval(
                script.getBytes(),
                ReturnType.BOOLEAN,
                1,
                key.getBytes(),
                String.valueOf(fpp).getBytes(),
                String.valueOf(expectedInsertions).getBytes()
            )
        );
    }

    /**
     * 向布隆过滤器添加元素
     *
     * @param key 过滤器键
     * @param value 值
     * @return 是否添加成功
     */
    public boolean bloomAdd(String key, String value) {
        String script = "return redis.call('BF.ADD', KEYS[1], ARGV[1])";
        Long result = redisTemplate.execute((RedisCallback<Long>) connection ->
            connection.scriptingCommands().eval(
                script.getBytes(),
                ReturnType.INTEGER,
                1,
                key.getBytes(),
                value.getBytes()
            )
        );
        return Long.valueOf(1).equals(result);
    }

    /**
     * 判断元素是否可能存在于布隆过滤器中
     *
     * @param key 过滤器键
     * @param value 值
     * @return 是否可能存在
     */
    public boolean bloomExists(String key, String value) {
        String script = "return redis.call('BF.EXISTS', KEYS[1], ARGV[1])";
        Long result = redisTemplate.execute((RedisCallback<Long>) connection ->
            connection.scriptingCommands().eval(
                script.getBytes(),
                ReturnType.INTEGER,
                1,
                key.getBytes(),
                value.getBytes()
            )
        );
        return Long.valueOf(1).equals(result);
    }

    // ============================== 限流器 ==============================

    /**
     * 滑动窗口限流
     *
     * @param key 限流键
     * @param window 时间窗口（秒）
     * @param limit 限制次数
     * @return 是否允许访问
     */
    public boolean isAllowed(String key, int window, int limit) {
        long now = System.currentTimeMillis();
        String script = 
            "redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1])" +
            "local count = redis.call('ZCARD', KEYS[1])" +
            "if tonumber(count) < tonumber(ARGV[3]) then " +
            "  redis.call('ZADD', KEYS[1], ARGV[2], ARGV[2])" +
            "  redis.call('EXPIRE', KEYS[1], ARGV[4])" +
            "  return 1 " +
            "end " +
            "return 0";
        
        Long result = redisTemplate.execute((RedisCallback<Long>) connection ->
            connection.scriptingCommands().eval(
                script.getBytes(),
                ReturnType.INTEGER,
                1,
                key.getBytes(),
                String.valueOf(now - window * 1000).getBytes(),
                String.valueOf(now).getBytes(),
                String.valueOf(limit).getBytes(),
                String.valueOf(window).getBytes()
            )
        );
        return Long.valueOf(1).equals(result);
    }

    // ============================== 缓存保护 ==============================

    /**
     * 获取缓存（带穿透保护）
     * 当查询结果为空时，缓存空值，避免缓存穿透
     *
     * @param key 键
     * @param callback 回调函数
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 值
     */
    public <T> T getWithPenetrationProtect(String key, CacheCallback<T> callback, long timeout, TimeUnit unit) {
        Object value = get(key);
        if (value != null) {
            return value == NULL_VALUE ? null : (T) value;
        }

        T newValue = callback.get();
        set(key, newValue == null ? NULL_VALUE : newValue, timeout, unit);
        return newValue;
    }

    private static final Object NULL_VALUE = new Object();

    /**
     * 获取缓存（带击穿保护）
     * 使用分布式锁防止缓存击穿
     *
     * @param key 键
     * @param callback 回调函数
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 值
     */
    public <T> T getWithBreakdownProtect(String key, CacheCallback<T> callback, long timeout, TimeUnit unit) {
        Object value = get(key);
        if (value != null) {
            return (T) value;
        }

        String lockKey = "lock:" + key;
        String requestId = UUID.randomUUID().toString();
        
        try {
            // 获取分布式锁
            if (tryLock(lockKey, requestId, 10, TimeUnit.SECONDS)) {
                // 双重检查
                value = get(key);
                if (value != null) {
                    return (T) value;
                }

                // 执行回调
                T newValue = callback.get();
                set(key, newValue, timeout, unit);
                return newValue;
            }
            
            // 等待一段时间后重试
            Thread.sleep(100);
            return getWithBreakdownProtect(key, callback, timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取缓存失败", e);
        } finally {
            releaseLock(lockKey, requestId);
        }
    }

    /**
     * 获取缓存（带雪崩保护）
     * 通过随机过期时间防止缓存雪崩
     *
     * @param key 键
     * @param callback 回调函数
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 值
     */
    public <T> T getWithAvalancheProtect(String key, CacheCallback<T> callback, long timeout, TimeUnit unit) {
        Object value = get(key);
        if (value != null) {
            return (T) value;
        }

        T newValue = callback.get();
        // 增加随机过期时间，防止同时过期
        long randomTimeout = timeout + new Random().nextInt(60);
        set(key, newValue, randomTimeout, unit);
        return newValue;
    }

    /**
     * 获取缓存（带完整保护）
     * 组合使用穿透、击穿、雪崩保护
     *
     * @param key 键
     * @param callback 回调函数
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 值
     */
    public <T> T getWithFullProtect(String key, CacheCallback<T> callback, long timeout, TimeUnit unit) {
        Object value = get(key);
        if (value != null) {
            return value == NULL_VALUE ? null : (T) value;
        }

        String lockKey = "lock:" + key;
        String requestId = UUID.randomUUID().toString();
        
        try {
            if (tryLock(lockKey, requestId, 10, TimeUnit.SECONDS)) {
                value = get(key);
                if (value != null) {
                    return value == NULL_VALUE ? null : (T) value;
                }

                T newValue = callback.get();
                // 增加随机过期时间，防止同时过期
                long randomTimeout = timeout + new Random().nextInt(60);
                set(key, newValue == null ? NULL_VALUE : newValue, randomTimeout, unit);
                return newValue;
            }
            
            Thread.sleep(100);
            return getWithFullProtect(key, callback, timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取缓存失败", e);
        } finally {
            releaseLock(lockKey, requestId);
        }
    }

    /**
     * 根据模式删除缓存
     *
     * @param pattern 模式
     */
    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 入队 - 向队列尾部添加元素
     *
     * @param key     队列键名
     * @param value   元素值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void enqueue(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForList().rightPush(key, value);
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 出队 - 从队列头部移除并返回元素
     *
     * @param key 队列键名
     * @return 队列头部元素
     */
    public Object dequeue(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 获取队列大小
     *
     * @param key 队列键名
     * @return 队列长度
     */
    public long size(String key) {
        return redisTemplate.opsForList().size(key);
    }
}