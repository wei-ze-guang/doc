package lock;

import org.junit.Test;

/**
 * 演示死锁
 * 条件
 * 1  互斥条件，资源是互斥使用的
 * 2. 占有和等待 请求别的资源时候等不到满足页不释放自己已经占有的资源
 * 3. 不可剥夺条件 ，资源只能占有者释放
 * 4.环路条件
 */
public class DeadlockDemo {

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    private static volatile boolean mutex1 = false;
    private static volatile boolean mutex2 = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Thread-1: locked lock1");
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                synchronized (lock2) {
                    System.out.println("Thread-1: locked lock2");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Thread-2: locked lock2");
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                synchronized (lock1) {
                    System.out.println("Thread-2: locked lock1");
                }
            }
        });

        t1.start();
        t2.start();
    }
    @Test
    public void testDeadlock() {
        Thread t1 = new Thread(() -> {
            mutex1 = true;
            System.out.println("Thread 1 got mutex1");

            while (!mutex2) {
                // 等待 mutex2
            }

            System.out.println("Thread 1 waiting for mutex2... (will deadlock)");
        });

        Thread t2 = new Thread(() -> {
            mutex2 = true;
            System.out.println("Thread 2 got mutex2");

            while (!mutex1) {
                // 等待 mutex1
            }

            System.out.println("Thread 2 waiting for mutex1... (will deadlock)");
        });

        t1.start();
        t2.start();
    }
}

