package juc;

import java.util.concurrent.SynchronousQueue;

public class SynchronousQueueExample {

    public static void main(String[] args) {
        SynchronousQueue<String> queue = new SynchronousQueue<>();

// 生产者线程
        new Thread(() -> {
            try {
                System.out.println("准备放入数据");
                queue.put("hello"); // 阻塞，直到消费者接收
                System.out.println("数据已传递");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

// 消费者线程
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 模拟延迟
                String data = queue.take(); // 阻塞直到生产者放数据
                System.out.println("接收到数据：" + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}

/**
 *
 * 多线程同时调用 put() 时，只有一个线程的 put() 会和某个 take() 配对成功，其他线程的 put() 都会阻塞等待，直到有对应的 take()。
 * | 方法           | 行为特点                     |
 * | ------------ | ------------------------ |
 * | `put(E e)`   | 阻塞直到有线程调用 `take()` 来接收元素 |
 * | `take()`     | 阻塞直到有线程调用 `put()` 传递元素   |
 * | `offer(E e)` | 尝试立即传递元素，若无消费者则返回失败      |
 * | `poll()`     | 尝试立即接收元素，若无生产者则返回空       |
 *
 * 使用场景
 * 线程间直接传递任务或数据，不需要缓存或排队。
 * 典型用例：线程池中的任务提交机制（例如 ThreadPoolExecutor 的内部工作原理）
 * 用于高效的线程间协作，减少中间存储。
 *
 * 简单来说，SynchronousQueue 是一个没有缓冲区的队列，生产者必须等待消费者接收，消费者必须等待生产者生产，是一种“同步交接”的工具。
 */
