package juc;

import java.util.Random;
import java.util.concurrent.*;

public class LinkedBlockingQueueExample {

    private static Random random = new Random();

    private static ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();


    //默认无界（但可以指定大小）
    static  BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);


    private static final int times = 100;  //循环次数

    private static final int threads = 200; // 线程数量

    static final CountDownLatch  latch = new CountDownLatch(threads ); // 一共启动了 threads*2 个线程

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
 * 非常好！你不只是想知道方法怎么用，还想知道**每种方法的特性、会不会阻塞、有啥坑、有啥优缺点**，我来系统地帮你梳理下 `LinkedBlockingQueue` 的核心用法和底层特点，确保你用了就理解透。
 *
 * ---
 *
 * ## 🔍 一、队列基本特性（LinkedBlockingQueue）
 *
 * * **基于链表结构**，每个元素是一个节点，不像 `ArrayBlockingQueue` 那样用数组。
 * * **容量有限**，默认最大是 `Integer.MAX_VALUE`，可以通过构造方法传容量。
 * * **线程安全**，入队和出队各自用一把锁，**支持并发的生产者和消费者同时进行**。
 * * **先进先出（FIFO）** 顺序。
 *
 * ---
 *
 * ## 🧠 二、方法分类 + 使用特性分析（优缺点说明）
 *
 * ### 🟩 入队方法（添加元素）
 *
 * | 方法                          | 阻塞性    | 特性                              | 是否推荐          | 可能的问题                | 优点          |
 * | --------------------------- | ------ | ------------------------------- | ------------- | -------------------- | ----------- |
 * | `add(E e)`                  | ❌ 非阻塞  | 满了就抛异常（`IllegalStateException`） | ❌ 少用          | 队列满就挂，影响稳定性          | 简单直接        |
 * | `offer(E e)`                | ❌ 非阻塞  | 添加失败返回 `false`，不抛异常             | ✅ 推荐          | 如果忘记处理 `false`，可能丢消息 | 不阻塞，稳定      |
 * | `offer(E e, timeout, unit)` | ⏳ 可选阻塞 | 等待指定时间，超时失败                     | ✅ 推荐在限时场景用    | 设置不当可能超时             | 可控、灵活       |
 * | `put(E e)`                  | ✅ 阻塞   | 队列满时会一直阻塞直到有空间                  | ✅ 推荐（限任务可控场景） | 无法及时中断会卡住线程          | 安全、稳定，适合生产者 |
 *
 * #### 🚩 注意：
 *
 * * `put()` 一直阻塞可能造成“线程卡死”。
 * * `add()` 不适合并发下使用，容易抛异常。
 *
 * ---
 *
 * ### 🟨 出队方法（获取元素）
 *
 * | 方法                    | 阻塞性    | 特性                 | 是否推荐      | 可能的问题          | 优点        |
 * | --------------------- | ------ | ------------------ | --------- | -------------- | --------- |
 * | `poll()`              | ❌ 非阻塞  | 没有就返回 `null`       | ✅ 推荐      | 没数据时容易空轮询      | 快速响应，不阻塞  |
 * | `poll(timeout, unit)` | ⏳ 可选阻塞 | 等一定时间，没取到返回 `null` | ✅ 推荐      | 时间设置不当会卡住或太快退出 | 灵活、适合控制超时 |
 * | `take()`              | ✅ 阻塞   | 队列为空时阻塞等待          | ✅ 推荐（消费者） | 不退出时会一直挂着      | 简单、易用     |
 * | `peek()`              | ❌ 非阻塞  | 查看头元素但不移除          | ✅ 辅助使用    | 多线程可能看完后被别人取走  | 非破坏性查看    |
 *
 * #### 🚩 注意：
 *
 * * `take()` 会无限阻塞，如果你没计划退出线程，要小心“死等”。
 * * `poll()` 要注意可能会反复拿到 null，导致“空转”浪费 CPU。
 *
 * ---
 *
 * ### 🟦 其他辅助方法
 *
 * | 方法                   | 特性       | 是否线程安全           | 备注                 |
 * | -------------------- | -------- | ---------------- | ------------------ |
 * | `size()`             | 获取当前队列长度 | 是，但可能不准确（瞬间可能变化） | 用作监控参考，不可用于流程判断    |
 * | `clear()`            | 清空所有元素   | 是                | 清除所有数据，小心误删        |
 * | `contains(Object o)` | 判断是否包含元素 | 是                | 遍历全队列，性能 O(n)      |
 * | `toArray()`          | 转为数组     | 是                | 会复制队列，避免并发修改影响     |
 * | `iterator()`         | 遍历元素     | 是（弱一致）           | 适合日志或快照用途，不保证完整准确性 |
 *
 * ---
 *
 * ## ⚖️ 三、总结对比：使用时如何选择方法？
 *
 * | 场景         | 推荐入队方法              | 推荐出队方法                     | 说明            |
 * | ---------- | ------------------- | -------------------------- | ------------- |
 * | 不允许阻塞、性能优先 | `offer(e)`          | `poll()`                   | 非阻塞、快，适合实时性场景 |
 * | 允许等待，有限超时  | `offer(e, timeout)` | `poll(timeout)`            | 可控，适合高吞吐又需要容错 |
 * | 消费速度必须跟上生产 | `put(e)`            | `take()`                   | 简洁可靠，但要小心阻塞   |
 * | 日志、可视化遍历   | -                   | `iterator()` / `toArray()` | 非常规场景，不用于业务逻辑 |
 *
 * ---
 *
 * ## ❗ 常见坑点和注意事项
 *
 * ### 1. `take()` / `put()` 阻塞线程
 *
 * 如果队列永远满/空，调用这些方法的线程会**一直挂住**，要小心线程资源耗尽。
 *
 * ### 2. `size()` 是瞬时的
 *
 * 在多线程环境下，这个方法**不能用于流程控制逻辑**，因为值可能过时。
 *
 * ### 3. 使用 `offer` 要判断返回值
 *
 * 否则数据可能悄悄“丢失”，而你以为它加进去了。
 *
 * ### 4. 多线程遍历注意一致性
 *
 * 虽然 `iterator()` 是“弱一致”，但仍然不能用于“修改逻辑”，只适合监控用途。
 *
 * ---
 *
 * ## ✅ 如果你要应用到项目中
 *
 * * 推荐搭配 `ExecutorService + LinkedBlockingQueue` 构造线程池；
 * * 或用于 `生产者-消费者模型`；
 * * 想性能更高、内存开销更小的队列，可以考虑：
 *
 *   * `ArrayBlockingQueue`（数组结构）
 *   * `Disruptor`（高性能）
 *
 * ---
 *
 * 如果你希望我结合具体代码场景（比如线程池、自定义消费者模型等）举例说明这些方法怎么配合使用，也可以告诉我，我可以给你一份结构完整的项目示例代码。
 */
