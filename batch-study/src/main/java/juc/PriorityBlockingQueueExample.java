package juc;

import java.util.Random;
import java.util.concurrent.*;

public class PriorityBlockingQueueExample {

    private static Random random = new Random();

    private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();


    //
    static BlockingQueue<String> queue = new PriorityBlockingQueue<>(100);


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
                    queue.put(message);
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
                    message = queue.poll(5, TimeUnit.SECONDS);
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
 * | 特性          | 描述                                             |
 * | ----------- | ---------------------------------------------- |
 * | **基于优先级排序** | 元素按优先级排序，非 FIFO（默认按自然顺序，或自定义 Comparator）       |
 * | **无界队列**    | 它理论上没有容量限制，内部分配数组自动扩容                          |
 * | **线程安全**    | 内部使用 `ReentrantLock` + `Condition` 实现线程安全和阻塞特性 |
 * | **非阻塞插入**   | 插入操作不会阻塞，即使队列“满”（因为它是无界的）                      |
 * | **阻塞取出**    | 获取元素时，如果队列为空，会阻塞直到有元素可取                        |
 *
 * // 使用自然顺序（元素必须实现 Comparable）
 * PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<>();
 * // 指定初始容量
 * PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<>(initialCapacity);  //只是指定，还是无界的
 * // 指定自定义 Comparator
 * PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<>(initialCapacity, comparator);
 *
 * | 方法                     | 阻塞 | 描述                        |
 * | ---------------------- | -- | ------------------------- |
 * | `put(E e)`             | ❌  | 实际等同于 `offer`，不阻塞，因为是无界队列 |
 * | `offer(E e)`           | ❌  | 直接插入元素                    |
 * | `take()`               | ✅  | 队列为空时阻塞，直到可用元素出现          |
 * | `poll(long, TimeUnit)` | ✅  | 队列为空时阻塞一段时间               |
 * | `peek()`               | ❌  | 查看队首元素（不移除）               |
 *
 * 因为它是设计来用于优先级调度的：
 * 通常用来调度优先级任务，而不是做容量限制；
 * 如果需要容量限制，建议用 PriorityQueue + 自定义逻辑 或者自己封装队列。
 *
 * | 问题             | 答案                                  |
 * | -------------- | ----------------------------------- |
 * | 为什么用优先级队列？     | 让重要的任务先执行，适用于调度/抢占/实时场景             |
 * | 相同优先级的数据怎么处理？  | 默认是“无序处理”，不保证插入顺序                   |
 * | 如何实现优先级 + 顺序？  | 添加 `sequenceNumber` 并扩展 `compareTo` |
 * | 优先级值越小还是越大先处理？ | 默认是 **值小优先**，你也可以自定义 Comparator     |
 */