package pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class newFixedThreadPoolDemo {

    /**
     * 固定大小
     */
    ExecutorService pool = Executors.newFixedThreadPool(4);

    /**
     * 这个其实上面固定大小线程池的特殊，核心线程和最大线程都有一
     */
    ExecutorService singlePool = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        newFixedThreadPoolDemo demo = new newFixedThreadPoolDemo();

        for (int i = 0; i < 100; i++) {
            final  String message = String.valueOf(i);
                demo.pool.execute(new Thread(() -> {
                    System.out.println(message);
                }));
        }
    }

}

/**
 * | 属性     | 描述                          |
 * | ------ | --------------------------- |
 * | 核心线程数  | `nThreads`（固定）              |
 * | 最大线程数  | `nThreads`                  |
 * | 队列类型   | 无限长度的 `LinkedBlockingQueue` |
 * | 回收空闲线程 | 不会（线程长期存活）                  |
 *
 * 使用场景：
 * 适用于 稳定负载、长期执行任务 的情况
 * 比如日志处理、文件上传、订单处理等
 *
 * 默认线程工厂：Executors.defaultThreadFactory()
 */
