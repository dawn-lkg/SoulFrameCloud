package com.clm.common.core.task;

/**
 * @author 陈黎明
 * @date 2025/6/3 下午2:00
 */
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class TaskManager {
    private final ConcurrentHashMap<String, TaskInfo> taskMap = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    // 添加一个内部类来存储任务信息
    private static class TaskInfo {
        final CompletableFuture<?> future;
        final Thread taskThread;

        TaskInfo(CompletableFuture<?> future, Thread taskThread) {
            this.future = future;
            this.taskThread = taskThread;
        }
    }

    public String submitTask(String taskId, Runnable task) {
        // 使用原子引用来存储线程引用
        AtomicReference<Thread> taskThread = new AtomicReference<>();

        CompletableFuture<?> future = CompletableFuture.runAsync(() -> {
            // 保存当前线程引用
            taskThread.set(Thread.currentThread());

            try {
                task.run();
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("Task {} was interrupted", taskId);
                } else {
                    log.error("Task {} failed with error", taskId, e);
                }
            }
        }, executor);

        // 存储任务信息
        taskMap.put(taskId, new TaskInfo(future, taskThread.get()));

        return taskId;
    }

    public boolean cancelTask(String taskId) {
        TaskInfo taskInfo = taskMap.get(taskId);
        if (taskInfo != null) {
            // 先中断线程
            if (taskInfo.taskThread != null) {
                taskInfo.taskThread.interrupt();
            }

            // 然后取消future
            boolean cancelled = taskInfo.future.cancel(true);
            if (cancelled) {
                taskMap.remove(taskId);
                log.info("Task {} cancelled successfully", taskId);
                return true;
            }
        }
        return false;
    }

    public void shutdown() {
        // 取消所有任务
        taskMap.forEach((taskId, taskInfo) -> cancelTask(taskId));

        // 关闭执行器
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // 测试代码
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        TaskManager taskManager = new TaskManager();

        // 提交第一个任务
        taskManager.submitTask("1", () -> {
            try {
                log.info("线程1开始执行，将睡眠20秒");
                Thread.sleep(20000);
                log.info("线程1执行完成");
            } catch (InterruptedException e) {
                log.error("线程1被中断");
                // 不需要重新设置中断标志，因为我们希望任务结束
                return; // 直接返回，结束任务
            }
        });

        // 提交第二个任务
        taskManager.submitTask("2", () -> {
            try {
                log.info("线程2开始执行，将睡眠5秒");
                Thread.sleep(5000);
                log.info("线程2执行完成");
            } catch (InterruptedException e) {
                log.error("线程2被中断");
                return; // 直接返回，结束任务
            }
        });

        // 10秒后取消任务1
        scheduler.schedule(() -> {
            log.info("准备取消线程1");
            taskManager.cancelTask("1");
        }, 10, TimeUnit.SECONDS);

        // 运行30秒后结束程序
        try {
            Thread.sleep(30000);
            log.info("程序准备结束");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            taskManager.shutdown();
            scheduler.shutdown();
        }
    }
}
