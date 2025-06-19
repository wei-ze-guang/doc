package juc;

import java.util.Random;
import java.util.concurrent.*;

public class ArrayBlockingQueueExample {

    private static Random random = new Random();

    private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();


    //一定要指定大小，可以设置公平和非公平所
    static BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000, true);


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
 * | 方法                                        | 是否阻塞       | 行为                           |
 * | ----------------------------------------- | ---------- | ---------------------------- |
 * | `add(E e)`                                | ❌ 抛异常      | 满时抛出 `IllegalStateException` |
 * | `offer(E e)`                              | ❌ 返回 false | 满时立即返回 false                 |
 * | `offer(E e, long timeout, TimeUnit unit)` | ✅          | 在超时内等待空位                     |
 * | `put(E e)`                                | ✅ 阻塞       | 队列满时阻塞直到有空位                  |
 *
 * 🔎 注意事项：
 * add() 不推荐在高并发下用，容易抛异常
 * put() 更适合多线程生产者使用，线程会挂起直到能添加
 *
 * | 方法                                  | 是否阻塞      | 行为                            |
 * | ----------------------------------- | --------- | ----------------------------- |
 * | `poll()`                            | ❌ 返回 null | 空时返回 null，不阻塞                 |
 * | `poll(long timeout, TimeUnit unit)` | ✅         | 空时等待一段时间                      |
 * | `take()`                            | ✅ 阻塞      | 空时阻塞直到有数据可取                   |
 * | `remove()`                          | ❌ 抛异常     | 空时抛出 `NoSuchElementException` |
 *
 * 推荐用：
 * take() 搭配 put() 使用最安全（生产-消费模型）
 * poll() 搭配 offer() 用于非阻塞尝试添加或获取
 *
 * | 方法                    | 说明                 |
 * | --------------------- | ------------------ |
 * | `peek()`              | 查看队首元素，不移除         |
 * | `size()`              | 当前队列中元素个数（非实时，不精确） |
 * | `remainingCapacity()` | 剩余容量               |
 * | `contains(Object o)`  | 是否包含指定元素           |
 * | `clear()`             | 清空队列（线程不安全）        |
 * | `toArray()`           | 转换为数组              |
 *
 * | 优点       | 说明                 |
 * | -------- | ------------------ |
 * | ✅ 简单高效   | 数组结构，访问效率高于链表      |
 * | ✅ 固定容量   | 控制内存使用，适合资源受限环境    |
 * | ✅ 支持阻塞   | 提供阻塞方法，适合生产者-消费者模型 |
 * | ✅ 支持公平策略 | 可设置为公平队列，避免线程饥饿    |
 *
 * ✅ 三、使用注意事项
 * 容量限制： 必须指定固定容量，添加元素超过容量时会阻塞或失败。
 *
 * 线程安全： 内部使用可重入锁（ReentrantLock） + 条件队列（Condition）实现。
 *
 * 公平性问题： 默认是非公平锁，性能好但可能导致“线程饿死”；可以通过构造方法设置为公平。
 *
 * 性能开销： 所有操作都使用同一个锁，吞吐量比 LinkedBlockingQueue 稍低。
 *
 * 不适合动态扩容场景： 如果任务不稳定或数据突发增长，可能导致频繁阻塞或失败。
 */