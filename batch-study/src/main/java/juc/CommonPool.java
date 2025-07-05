package juc;

import java.util.concurrent.*;

/**
 * 使用DCL 双重检测机制
 * 创建一个线程锁池
 */
public class CommonPool {

    private static volatile ExecutorService instance;

    private static volatile ExecutorService cachedThreadPool;

    public static ExecutorService getInstance() {
        /**
         * 这里是为了防止this飘逸，半初始化
         */
        if (instance == null) {
            /**
             * 万一多个线程在这一步看到都是null，就会都进来，所以里面就需要判断一次
             */
            synchronized (CommonPool.class) {
                /**
                 * 这里为什么还要判断一次呢，看上面
                 */
                if (instance == null) {
                    instance = new ThreadPoolExecutor(
                            20,
                            20,
                            100,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(10),
                            Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }

        return instance;
    }


    /**
     * 下面床架一个缓存线程池
     * @return
     */
    public static ExecutorService getCachedThreadPool() {
        /**
         * 这里是为了防止this飘逸，半初始化
         */
        if (cachedThreadPool == null) {
            /**
             * 万一多个线程在这一步看到都是null，就会都进来，所以里面就需要判断一次
             */
            synchronized (CommonPool.class) {
                /**
                 * 这里为什么还要判断一次呢，看上面
                 */
                if (cachedThreadPool == null) {
                    cachedThreadPool= new ThreadPoolExecutor(
                            20,
                            Integer.MAX_VALUE,
                            100,
                            TimeUnit.SECONDS,
                            new SynchronousQueue<>(),
                            Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }

        return cachedThreadPool;
    }

}
