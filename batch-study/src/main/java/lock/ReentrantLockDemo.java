package lock;

import org.junit.Test;

import java.time.Instant;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock使用
 */
public class ReentrantLockDemo {

    private static ReentrantLock lock = new ReentrantLock();

    private static void test(){
        lock.lock();
        try {
            Instant now = Instant.now();
            System.out.println(Thread.currentThread().getName());

        }finally {
            lock.unlock();
        }
    }

    private static void test2(){
        if(lock.tryLock()){
            try {
                Instant now = Instant.now();
                System.out.println(Thread.currentThread().getName()+"获得锁");
            }finally {
                lock.unlock();
            }
        }
        else System.out.println(Thread.currentThread().getName());
    }

    private static void test3(){
        try {
            if (lock.tryLock(200, TimeUnit.MICROSECONDS)) {  // 最多等2秒
                try {
                    // 成功拿到锁，执行业务逻辑
                    System.out.println(Thread.currentThread().getName()+"拿到锁");
                } finally {
                    lock.unlock();
                }
            } else {
                // 超时未拿到锁，执行超时处理
                System.out.println("没拿到锁，放弃执行");
            }
        } catch (InterruptedException e) {
            // 响应中断，中断等待锁的线程
            System.out.println("等待锁时被中断");
            Thread.currentThread().interrupt(); // 恢复中断状态
        }
    }

    private static void testInterrupted(){
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            System.out.println("线程被中断");
        }

    }

    private static void testInterrupted1(){
        try {
            lock.lock();
        } finally {
            lock.unlock();
        }

    }

    static class TestInterrupted implements Runnable{

        @Override
        public void run() {
                testInterrupted();
                System.out.println("执行完毕");
        }
    }

    @Test
    public void testInterrupt()  {
        Map<Integer,Thread> map = new Hashtable<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new TestInterrupted());
            map.put(i, thread);
            thread.start();
        }
        for (Map.Entry<Integer,Thread> entry : map.entrySet()) {
//            Thread thread = entry.getValue();
            if(entry.getKey() == 1){
                entry.getValue().interrupt();
            }
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void test1(){
        for (int i = 0; i < 10; i++) {
            Runnable runnable = ()->test3();
            new Thread(runnable).start();
        }
    }
}
/**
 * | 方法名                            | 作用                                      |
 * | ------------------------------ | --------------------------------------- |
 * | `lock()`                       | 获取锁（**阻塞**，不可中断）                        |
 * | `lockInterruptibly()`          | 获取锁（阻塞，但可响应中断）                          |
 * | `tryLock()`                    | 尝试获取锁（**立即返回** true/false）              |
 * | `tryLock(long time, TimeUnit)` | 尝试获取锁，**最多等待一定时间**                      |
 * | `unlock()`                     | 释放锁（必须与 `lock()` 配对）                    |
 * | `isHeldByCurrentThread()`      | 当前线程是否持有该锁                              |
 * | `isLocked()`                   | 是否有任何线程持有锁                              |
 * | `getHoldCount()`               | 当前线程持有锁的**重入次数**（0 表示未持有）               |
 * | `getQueueLength()`             | 正在等待获取该锁的线程数量                           |
 * | `hasQueuedThreads()`           | 是否有线程正在等待该锁                             |
 * | `hasQueuedThread(Thread)`      | 指定线程是否在等待该锁                             |
 * | `isFair()`                     | 是否是公平锁                                  |
 * | `newCondition()`               | 获取 `Condition` 条件对象（配合 await/signal 使用） |
 */
