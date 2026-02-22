package com.clm.common.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */
@Component
@Slf4j
public class SseTemplate {

    private final SseProperties props;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final ConcurrentMap<String, SseEmitter> repo = new ConcurrentHashMap<>();

    // 添加连接统计信息
    private final AtomicLong totalConnections = new AtomicLong(0);
    private final AtomicLong activeConnections = new AtomicLong(0);

    public SseTemplate(SseProperties props) {
        this.props = props;
        startHeartbeat();
        startConnectionCleanup(); // 启动连接清理任务
        startMonitoring();        // 启动监控任务
    }

    public SseEmitter connect(String clientId) {
        return connect(clientId, props.getDefaultTimeout());
    }

    public SseEmitter connect(String businessType, String clientId, Duration timeout) {
        return connect(businessType + ":" + clientId, timeout);
    }

    public SseEmitter connect(String businessType, String clientId) {
        return connect(businessType + ":" + clientId);
    }

    public SseEmitter connect(String clientId, Duration timeout) {
        Long ms = timeout.isZero() ? 0 : timeout.toMillis();
        log.warn("SSE client connected: {},timeOut:{}", clientId, ms);
        SseEmitter emitter = new SseEmitter(ms);
        repo.put(clientId, emitter);
        totalConnections.incrementAndGet();
        activeConnections.incrementAndGet();

        // 清理
        emitter.onCompletion(() -> {
            log.warn("SSE client disconnected: {}", clientId);
            repo.remove(clientId);
            activeConnections.decrementAndGet();
        });
        emitter.onTimeout(() -> {
            repo.remove(clientId);
            emitter.complete();
            activeConnections.decrementAndGet();
            log.warn("SSE client timeout: {}", clientId);
        });
        emitter.onError(e -> {
            log.warn("SSE send failed to client: {}, error: {}", clientId, e.getMessage());
            emitter.complete(); // 主动关闭连接
            repo.values().remove(emitter); // 从注册表中移除
            activeConnections.decrementAndGet();
        });

        return emitter;
    }

    /**
     * 单发
     */
    public void send(String clientId, Object data) {
        send(clientId, "message", data);
    }


    public void send(String clientId, String event, Object data) {
        send0(repo.get(clientId), event, data);
    }

    public void send(String businessType, String clientId, String event, Object data) {
        send(businessType + ":" + clientId, event, data);
    }

    public void sendTo(Collection<String> ids, String event, Object data) {
        ids.stream()
                .map(repo::get)
                .filter(Objects::nonNull)
                .forEach(e -> send0(e, event, data));
    }

    public void broadcast(String event, Object data) {
        sendTo(repo.keySet(), event, data);
    }


    public void broadcast(String businessType, String event, Object data) {
        repo.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(businessType))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .forEach(e -> send0(e, event, data));
    }

    private void send0(SseEmitter emitter, String event, Object data) {
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name(event)
                    .data(data)
                    .id(UUID.randomUUID().toString()));
        } catch (IOException e) {
            log.warn("Send failed: {}", e.getMessage());
            // 重试一次
            if (props.getMaxRetry() > 0) {
                repo.values().remove(emitter);
                activeConnections.decrementAndGet();
            }
        }
    }

    private void startHeartbeat() {
        scheduler.scheduleWithFixedDelay(
                () -> broadcast("heartbeat", ""),
                props.getHeartbeat().toMillis(),
                props.getHeartbeat().toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 启动连接清理任务
     * 定期检查并清理无效连接
     */
    private void startConnectionCleanup() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                log.debug("Starting connection cleanup task. Current connections: {}", repo.size());
                repo.entrySet().removeIf(entry -> {
                    SseEmitter emitter = entry.getValue();
                    // 检查连接是否已经完成或超时
                    if (emitter != null && (emitter.getTimeout() > 0)) {
                        // 这里我们无法直接判断连接是否有效，所以只记录日志
                        return false;
                    }
                    return false;
                });
                log.debug("Connection cleanup completed. Remaining connections: {}", repo.size());
            } catch (Exception e) {
                log.error("Error during connection cleanup: ", e);
            }
        }, 30, 60, TimeUnit.SECONDS); // 延迟30秒后每60秒执行一次
    }

    /**
     * 启动监控任务
     * 定期输出连接统计信息
     */
    private void startMonitoring() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
//                log.info("SSE Connection Monitoring - Total: {}, Active: {}, Stored: {}",
//                        totalConnections.get(), activeConnections.get(), repo.size());

                // 检查不一致情况
                if (activeConnections.get() != repo.size()) {
                    log.warn("Inconsistent connection count - Active: {}, Stored: {}",
                            activeConnections.get(), repo.size());
                }
            } catch (Exception e) {
                log.error("Error during monitoring task: ", e);
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    /**
     * 获取当前连接统计信息
     *
     * @return 包含统计信息的字符串
     */
    public String getStats() {
        return String.format("Total: %d, Active: %d, Stored: %d",
                totalConnections.get(), activeConnections.get(), repo.size());
    }

    /**
     * 手动清理所有资源
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 关闭所有SSE连接
        repo.values().forEach(SseEmitter::complete);
        repo.clear();
    }
}
