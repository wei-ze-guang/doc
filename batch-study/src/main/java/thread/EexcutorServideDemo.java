package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EexcutorServideDemo {

    private static final AtomicInteger count = new AtomicInteger(0);

    private static final int forTimes = 200;

    private static final Long sleepTime = 500L;

    private static final int threads = 1000;

    private static final int limitThreads = 100;

    private static ThreadLocalRandom rand = ThreadLocalRandom.current();

    public static void main(String[] args) throws InterruptedException {
//        ExecutorService es = getFixedThreadPool(5);
        ExecutorService es = getCachedThreadPool();

        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();

        int maxThreads = 5;

        for (int i = 0; i < threads; i++) {
            try {
                futures.add(es.submit(new CallableImpl()));
                maxThreads++;
            }
            catch (Exception e) {
                System.out.println("达到最大数量");
            }

            if (maxThreads > limitThreads) {
                es.shutdown();
            }

        }


        System.out.println("等待全部线程执行完毕");

        // 获取每个任务的结果
        for (Future<Integer> future : futures) {
            try {
                Integer result = future.get();  // 获取任务返回的结果,同步的
                System.out.println("任务返回的结果: " + result);
            } catch (ExecutionException e) {
                e.printStackTrace();  // 如果任务执行时抛出异常
            }
        }
        System.out.println("准备关闭线程池");
        es.shutdown();
        System.out.println("等待全部线程执行完毕");
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        System.out.println("所有任务执行完毕，线程池关闭");
    }
    // 获取线程池状态信息
    static void getThreadPoolStatus(ThreadPoolExecutor executor) {
        System.out.println("线程池当前状态：");
        System.out.println("已提交任务数: " + executor.getTaskCount());
        System.out.println("已完成任务数: " + executor.getCompletedTaskCount());
        System.out.println("当前正在执行的任务数: " + executor.getActiveCount());
        System.out.println("线程池中核心线程数: " + executor.getCorePoolSize());
        System.out.println("线程池中最大线程数: " + executor.getMaximumPoolSize());
        System.out.println();
    }


    /**
     * 如果传入的是1  就是一个 Executors.newSingleThreadExecutor()等效
     * @param nThreads
     * @return
     */
    static ExecutorService getFixedThreadPool(int nThreads) {
        ExecutorService pool = new ThreadPoolExecutor(
                nThreads,
                nThreads,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
                return pool;
    }
    static ExecutorService getCachedThreadPool() {
        ExecutorService service = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                30L,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return service;
    }
    static void testFixedThreadPool(int nThreads) {
        ExecutorService pool = getFixedThreadPool(nThreads);
    }

    static class CallableImpl implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            for (int i = 0; i < forTimes; i++) {
                int ran = rand.nextInt(2);
                if (ran == 0)
                    count.decrementAndGet();
                else
                    count.incrementAndGet();
            }
            Integer result = count.get();
            System.out.println(Thread.currentThread().getName() + ": " + result);
            return result;
        }
    }
}
