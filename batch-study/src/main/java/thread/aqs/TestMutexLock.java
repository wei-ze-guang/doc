package thread.aqs;

public class TestMutexLock {
    public static void main(String[] args) throws InterruptedException {

        MutexLock mutexLock = new MutexLock();

        // 创建多个线程来测试互斥锁
        Runnable task = () -> {
            try {
                mutexLock.lock();  // 获取锁
                System.out.println(Thread.currentThread().getName() + " acquired the lock.");
                Thread.sleep(1000);  // 模拟执行一些操作
                System.out.println(Thread.currentThread().getName() + " releasing the lock.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                mutexLock.unlock();  // 释放锁
            }
        };

        // 创建并启动多个线程
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        // 等待线程执行完毕
        t1.join();
        t2.join();
        t3.join();
    }
}
