package juc;

import java.util.Random;
import java.util.concurrent.*;

public class DelayQueueExample {

    private static Random random = new Random();

    private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();


    //
    static DelayQueue queue = new DelayQueue();


    private static final int times = 100;  //循环次数

    private static final int threads = 200; // 线程数量

    static final CountDownLatch latch = new CountDownLatch(threads ); // 一共启动了 threads*2 个线程

    public static void main(String[] args) throws InterruptedException {



        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            new producerThread().start();
            consumerThread consumerThread = new consumerThread();
            consumerThread.setPriority(6);  //优先级，最小是5  默认是5  最高10
            consumerThread.start();
        }


        latch.await(); // 主线程阻塞，直到所有子线程 countDown

        long endTime = System.currentTimeMillis();

        System.out.println("用时:"+(endTime - startTime));

        System.out.println("所有线程执行完毕！");
    }


    /**
     * 生产者线程
     */
    static class producerThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
//                String message = String.valueOf(threadLocalRandom.nextInt());
                String message = String.valueOf(random.nextInt(100));
                System.out.println(Thread.currentThread().getName()+"线程在生产信息："+message);

                try {
                    queue.put(new MyTask(1000,TimeUnit.MILLISECONDS,message));
                }catch (Exception e) {
                    System.out.println("插入失败");
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            latch.countDown(); // 关键！！告诉主线程“我结束了”
        }
    }

    static class consumerThread extends Thread {
        @Override
        public void run() {
            while (true) {

                String message = null;
                try {
                    message = queue.take().toString();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(message != null) {
                    System.out.println("线程"+Thread.currentThread().getName()+"正在消费信息:"+message);
                }
                else {
                    try {
                        Thread.sleep(20);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

/**
 * MyTask 是一个延迟任务类，实现了 Delayed 接口，
 * 可用于 DelayQueue 队列中，用于控制任务延迟执行。
 */
class MyTask implements Delayed {

    // 表示任务的实际执行时间（绝对时间点，单位为纳秒）
    private long executeTime;

    private String taskDesc;  //内容

    /**
     * 构造函数：初始化一个延迟任务
     * @param delayTime 延迟时间
     * @param unit 延迟时间的单位（如秒、毫秒）
     */
    public MyTask(long delayTime, TimeUnit unit, String taskDesc) {
        // 当前时间 + 延迟时间 = 任务执行的时间点（单位纳秒）
        this.taskDesc = taskDesc;
        this.executeTime = System.nanoTime() + unit.toNanos(delayTime);
    }

    /**
     * 返回任务剩余的延迟时间
     * @param unit 你希望返回的时间单位（例如秒、毫秒）
     * @return 距离执行时间还有多久，如果 <= 0 则任务已过期可被取出
     */
    @Override
    public long getDelay(TimeUnit unit) {
        // 剩余延迟 = 执行时间 - 当前时间
        long diff = executeTime - System.nanoTime();
        // 将延迟时间转换为调用者希望的单位
        return unit.convert(diff, TimeUnit.NANOSECONDS);
    }

    /**
     * 比较两个 Delayed 对象的先后顺序，延迟队列不使用这个排序
     * @param other 另一个待比较的 Delayed 对象
     * @return -1 表示当前任务先执行，1 表示后执行
     */
//    @Override
//    public int compareTo(Delayed other) {
//        // 比较剩余延迟时间，谁短谁优先
//        if (this.getDelay(TimeUnit.NANOSECONDS) < other.getDelay(TimeUnit.NANOSECONDS)) {
//            return -1;
//        } else if (this.getDelay(TimeUnit.NANOSECONDS) > other.getDelay(TimeUnit.NANOSECONDS)) {
//            return 1;
//        } else {
//            return 0;
//        }
//    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.NANOSECONDS),
                o.getDelay(TimeUnit.NANOSECONDS));
    }

    @Override
    public String toString() {
        return "MyTask [executeTime=" + executeTime + ", taskDesc=" + taskDesc + "]";
    }
}

/**
 * ✅ 一、DelayQueue 是什么？
 * DelayQueue 是一个支持延迟获取元素的阻塞队列。
 * 只有当元素的延迟时间到达，才能从队列中取出它；
 * 实现方式：元素必须实现 Delayed 接口，内部其实用的是一个 最小堆（类似 PriorityQueue）进行排序。
 * 🌟 使用场景
 * 缓存系统：某个缓存对象在一定时间后过期；
 * 任务调度：延迟执行任务；
 * 限流/定时检查：比如检测“X 秒后无人响应就超时处理”；
 * 网络断线重连机制。
 *
 *
 * | 问题                            | 答案                                                 |
 * | ----------------------------- | -------------------------------------------------- |
 * | DelayQueue 是什么？               | 支持延迟出队的阻塞队列（只有过期的元素才能被取出）                          |
 * | 和普通队列最大区别？                    | 出队时间不受插入顺序控制，而是**受过期时间控制**                         |
 * | 使用注意点？                        | 元素必须实现 `Delayed`，并实现比较逻辑                           |
 * | 和 `PriorityBlockingQueue` 区别？ | `PriorityBlockingQueue` 立即可取；`DelayQueue` 要等延迟时间到了 |
 *
 * ✅ 补充：DelayQueue 的处理流程
 * 当你往 DelayQueue 添加 MyTask 时，队列会根据 compareTo 方法对任务排序。
 * 调用 queue.take()：
 * 如果 getDelay() 返回值 > 0，会阻塞；
 * 如果 ≤ 0，说明到了时间，就取出并返回该任务。
 *
 * 它适用于：
 * 任务调度器
 * 缓存过期清除
 * 定时消息推送
 */