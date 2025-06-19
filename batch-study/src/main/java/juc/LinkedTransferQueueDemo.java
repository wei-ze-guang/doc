package juc;

import java.util.concurrent.LinkedTransferQueue;

public class LinkedTransferQueueDemo {

    static LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();

    public static void main(String[] args) throws InterruptedException {
        // 消费者线程，阻塞等待接收数据
        Thread consumer = new Thread(() -> {
            try {
                System.out.println("消费者等待接收数据...");
                String msg = queue.take();
                System.out.println("消费者接收到: " + msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        consumer.start();

        Thread.sleep(100); // 确保消费者先启动，等待接收

        // 生产者线程，使用transfer，等待消费者接收
        Thread producer = new Thread(() -> {
            try {
                System.out.println("生产者通过transfer传递数据...");
                queue.transfer("Hello from transfer!");
                System.out.println("生产者传递完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producer.start();

        producer.join();
        consumer.join();

        // 使用tryTransfer示例
        boolean offered = queue.tryTransfer("Hello from tryTransfer!");
        System.out.println("tryTransfer是否成功直接交接给消费者？ " + offered);

        // 如果没有消费者等待，tryTransfer会返回false，说明没交接成功
    }
}

/**
 * | 特性       | LinkedBlockingQueue / ArrayBlockingQueue | LinkedTransferQueue      |
 * | -------- | ---------------------------------------- | ------------------------ |
 * | 队列容量     | 有界（可设置容量）                                | 无界                       |
 * | 生产者阻塞时机  | 队列满时阻塞                                   | 调用 transfer() 阻塞直到消费者接收  |
 * | 支持直接传递   | 不支持                                      | 支持（transfer、tryTransfer） |
 * | 是否支持即时消费 | 否                                        | 是                        |
 * | 适用场景     | 生产消费者异步缓冲                                | 生产者等待消费者确认的场景            |
 */

