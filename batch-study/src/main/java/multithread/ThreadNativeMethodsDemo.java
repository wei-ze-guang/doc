package multithread;

public class ThreadNativeMethodsDemo {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        // 线程1：演示 start0()，setNativeName，setPriority0，setDaemon
        Thread thread1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " started.");

            try {
                Thread.sleep(1000); // 主动阻塞，触发interrupt
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted during sleep.");
            }

            synchronized (lock) {
                // 判断当前线程是否持有lock锁
                boolean holdsLock = Thread.holdsLock(lock);
                System.out.println(Thread.currentThread().getName() + " holds lock? " + holdsLock);
            }

            System.out.println(Thread.currentThread().getName() + " finishing.");
        }, "CustomThread-1");

        // 设置线程为守护线程
        thread1.setDaemon(true);

        // 设置优先级（底层调用setPriority0）
        thread1.setPriority(Thread.MAX_PRIORITY);

        // 启动线程，底层调用start0()
        thread1.start();

        // 线程2：演示interrupt0()，isInterrupted()，yield()
        Thread thread2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " started.");

            int count = 0;
            while (count < 10) {
                // 让出执行权，底层调用yield()
                Thread.yield();

                // 查询是否中断（底层调用isInterrupted）
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " detected interrupt. Exiting loop.");
                    break;
                }
                System.out.println(Thread.currentThread().getName() + " working, count = " + count);
                count++;
            }
            System.out.println(Thread.currentThread().getName() + " finishing.");
        }, "CustomThread-2");

        thread2.start();

        // 主线程暂停500ms后中断线程2，底层调用interrupt0()
        Thread.sleep(500);
        thread2.interrupt();

        // 主线程等待线程1结束，底层调用join()（不直接是native，但涉及等待与唤醒）
        thread1.join();

        System.out.println("Main thread finished.");
    }
}

