package juc;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ConcurrentLinkedQueueExample {

    private static Random random = new Random();

    private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    private static final Queue<String> queue = new ConcurrentLinkedQueue<>();

    private static final int times = 100;  //循环次数

    private static final int threads = 1000; // 线程数量

    static final CountDownLatch  latch = new CountDownLatch(threads ); // 一共启动了 threads*2 个线程

    public static void main(String[] args) throws InterruptedException {

        Thread.sleep(10000);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            new producerThread().start();
//            new consumerThread().start();
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
                queue.offer(message);
                try {
                    Thread.sleep(10);
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

                String message = queue.poll();
                if(message != null) {
                    System.out.println("线程"+Thread.currentThread().getName()+"正在消费信息:"+message);
                }
                else {
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

/**
 * | 方法           | 功能说明                       | 特点              |
 * | ------------ | -------------------------- | --------------- |
 * | `offer(E e)` | 向队列尾部添加元素                  | 永远不会阻塞，推荐使用 比add安全    |
 * | `add(E e)`   | 添加元素，失败时抛异常                | 功能等价于 `offer()` |
 * | `poll()`     | 移除并返回队列头部元素，队列为空时返回 `null` | 非阻塞             |
 * | `peek()`     | 返回队列头部元素但不移除               | 非阻塞             |
 * | `isEmpty()`  | 判断队列是否为空                   | 线程安全            |
 * | `size()`     | 返回队列当前大小（近似值）              | 非精确，不能用于判断是否已满等 |
 * | `iterator()` | 返回迭代器（**弱一致性**）            | 不抛异常，但不保证实时性    |
 */

/**
 * 基于链表的无界非阻塞队列（CAS 实现）
 */


/**
 * 🧱 1. Random 是线程安全的吗？有锁的

 * private static Random random = new Random();
 * ✅ 线程安全：是的，Random 的每个方法如 nextInt() 等都有 synchronized。
 * ⚠️ 但存在性能问题：多个线程争用同一个 Random 实例时，会产生 锁竞争，导致效率降低。
 * 示例：如果多个线程共享一个 Random 对象，会排队进入 nextInt() 方法，严重拖慢速度。
 *
 * ⚡ 2. ThreadLocalRandom 是什么？ 无锁的
 * private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
 * ✅ 线程安全：是的，它专为多线程设计。
 * 💡 核心优势：每个线程都有自己的 Random 实例，不共享，所以没有锁竞争，性能非常高。
 * 🌱 使用 ThreadLocal 机制，每个线程初始化自己的随机数种子。
 */


/**
 * ✅ 最推荐方式：使用 CountDownLatch
 * 这是专门为“等待一批线程执行完”设计的。
 */