package pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class newScheduledThreadPoolDemo {
    static  ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(3);
    public static void main(String[] args) {

        // 延迟执行任务
        scheduledPool.schedule(() -> {
            System.out.println("延迟3秒执行");
        }, 3, TimeUnit.SECONDS);

// 周期执行任务：初次延迟1秒，之后每5秒执行一次
        scheduledPool.scheduleAtFixedRate(() -> {
            System.out.println("周期执行任务，每5秒一次");
        }, 1, 5, TimeUnit.SECONDS);

// 周期执行任务：任务执行完毕后间隔5秒才执行下一次
        scheduledPool.scheduleWithFixedDelay(() -> {
            System.out.println("执行完毕后延迟5秒再执行");
        }, 1, 5, TimeUnit.SECONDS);
    }
}

/**
 * 你会用它来做什么？
 * 定时刷新缓存
 * 心跳检测
 * 定时发送报告
 * 定时清理资源
 *
 * public ScheduledThreadPoolExecutor(int corePoolSize) {
 *     super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS,
 *           new DelayedWorkQueue());
 * }
 */
