package pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 这里演示触发拒绝策略
 */
public class RejectPool {

    static  ExecutorService executor = new ThreadPoolExecutor(
            2,                      // corePoolSize
            4,                      // maximumPoolSize
            60, TimeUnit.SECONDS,   // keepAliveTime
            new ArrayBlockingQueue<>(2), // 有界队列，容量为2   拒绝策略的话一定是有界的，不然怎么都不会拒绝
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy() // 拒绝策略：抛出异常
    );




    public static void main(String[] args){

        List<Future<?> >  futures = new ArrayList<Future<?>>();
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            final Future<?> future;

            future = executor.submit(() -> {
                System.out.println("执行任务：" + taskId);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            futures.add(future);
        }

        /**
         * Exception in thread "main" java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.FutureTask@10f87f48[Not completed, task = java.util.concurrent.Executors$RunnableAdapter@6f496d9f[Wrapped task = pool.RejectPool$$Lambda$15/0x000002512d001200@723279cf]] rejected from java.util.concurrent.ThreadPoolExecutor@b4c966a[Running, pool size = 4, active threads = 4, queued tasks = 2, completed tasks = 0]
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2065)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:833)
         * 	at java.base/java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1365)
         * 	at java.base/java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:123)
         * 	at pool.RejectPool.main(RejectPool.java:31)
         * 这里保存是产生了拒绝策略
         */

        /**
         * 尽管runnable是无返回值的，，但是可以获取结果
         */

       try {
           Thread.sleep(1000);
       }catch (InterruptedException e){

       }finally {
           futures.forEach((item) ->{
               try {
                   Object result = item.get();
                   //由于无返回值，返回是null
                   System.out.println(result);

               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               } catch (ExecutionException e) {
                   throw new RuntimeException(e);
               }
           });
       }
    }

}
