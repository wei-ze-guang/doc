package keyword;

import org.openjdk.jol.info.ClassLayout;

/**
 * 学习 synchronized   和MarkWord 头信息的一些练习
 * 是可以设置 synchronized  的锁状态变换的
 */
public class MarkWordSynchronizedDemo {
    static volatile Object obj = new Object();
    public static void main(String[] args) throws Exception {
        // 禁用偏向锁等待时间，方便测试（-XX:BiasedLockingStartupDelay=0）
//        Object obj = new Object();

        System.out.println("=== 初始状态（无锁） ===");
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());

        // 进入synchronized，尝试偏向锁
        synchronized (obj) {
            System.out.println("=== 偏向锁状态 ===");
            System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        }

        // 新线程竞争锁，模拟轻量级锁（竞争）
        Thread competitor = new Thread(() -> {
            synchronized (obj) {
                System.out.println("=== 轻量级锁（竞争） ===");
                System.out.println(ClassLayout.parseInstance(obj).toPrintable());
            }
        });

        competitor.start();
        competitor.join();

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                synchronized (obj) {
                    try {
                        Thread.sleep(500);  // 增加锁竞争时间窗口
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(ClassLayout.parseInstance(obj).toPrintable());
                }
            }).start();
        }

        // 主线程再次加锁
        synchronized (obj) {
            System.out.println("=== 主线程再锁定 ===");
            System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        }
    }
}
