package pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class newCachedThreadPoolDemo {
    /**
     *
     */
    private static ExecutorService pool = Executors.newCachedThreadPool();


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(String.valueOf(finalI));}
            });

        }
    }
}

/**
 * newCachedThreadPool() 是什么？
 * 一个“弹性”线程池，线程数量不是固定的，会根据任务需求自动增长和缩减。
 * 当有新任务来时：
 * 如果有空闲线程，就用空闲线程执行任务。
 * 如果没有空闲线程，则创建新的线程来执行任务（线程数量可以无限增长，理论上没有上限）。
 * 空闲线程如果60秒没有被使用，会被自动回收（销毁），所以线程池不会无限膨胀，保持资源节约。  这个60s是写死了的，除非自己创建一个类似的
 * 适合执行很多短时间异步任务，线程可以快速创建和销毁。
 *
 * 可以自己写一个和这个一摸一样的 ，但是存活时间不一样
 *
 * 它的工作原理
 * 任务来了，先找空闲线程用，没有就新建线程。
 * 线程空闲超过60秒就干掉，保持线程数量合理。
 * 队列使用的是 直接提交队列（SynchronousQueue），不存储任务，任务必须直接交给线程处理。
 *
 * ThreadPoolExecutor cachedThreadPool = new ThreadPoolExecutor(
 *     0,                          // corePoolSize
 *     Integer.MAX_VALUE,           // maximumPoolSize
 *     30,                         // keepAliveTime   //如果把这里改为60  就是和上面一摸一样了
 *     TimeUnit.SECONDS,            // 时间单位
 *     new SynchronousQueue<Runnable>(), // 直接提交队列  //这里是来一个接一个
 *     Executors.defaultThreadFactory(),
 *     new ThreadPoolExecutor.AbortPolicy()
 * );
 */
