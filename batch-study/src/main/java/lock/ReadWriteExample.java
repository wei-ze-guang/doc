package lock;

import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteExample {
    private int data = 0; // 要保护的数据
    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static final Lock readLock = rwLock.readLock();

    private static final Lock writeLock = rwLock.writeLock();

    private final ReentrantLock lock = new ReentrantLock();

    // 读操作
    public int readData() {
        readLock.lock(); // 获取读锁
        try {
            System.out.println(Thread.currentThread().getName() + " 正在读取数据: " + data);
            Thread.sleep(10); // 模拟读操作
            return data;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            readLock.unlock(); // 释放读锁
        }
    }

    // 写操作
    public void writeData(int value) {
        writeLock.lock(); // 获取写锁
        try {
            System.out.println(Thread.currentThread().getName() + " 正在写入数据: " + value);
            Thread.sleep(100); // 模拟写操作
            data = value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock(); // 释放写锁
        }
    }


    public static void test(){
        //演示锁降级，这个锁是支持获取了写锁之后内部可以获取读锁的
        writeLock.lock();
        try{
            System.out.printf("处理数据中");
            Thread.sleep(100);
            //释放写锁之前先获取读锁
            readLock.lock();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally{
            writeLock.unlock();
        }

        try {
            System.out.printf("获取了读锁现在读取资源");
        }finally {
            readLock.unlock();
        }
    }

    /**
     * 先获取数据，如果数据为空，然后写入数据，写入数据之后再获取读锁，
     */
    public static void testCache(){
        Object cache = null;
        if (cache == null) {
            writeLock.lock();
            try {
                if (cache == null) {
                    cache = new Object(); // 写入缓存
                    readLock.lock();      // 锁降级
                }
            } finally {
                writeLock.unlock();       // 降级：从写锁转为读锁
            }

            try {
                System.out.printf("使用数据cache");               // 后续只读
            } finally {
                readLock.unlock();
            }
        }

        System.out.println("返回数据:"+cache);

    }
    @Test
    public void t() throws ExecutionException, InterruptedException {
        //下面是有返回值的callable
        Future<String> task = new FutureTask<>(()->{
            test();
            return "动态的返回值";
        });

        for (int i = 0; i < 20; i++) {
            Runnable runnable = () -> testCache();
            FutureTask<String> task2 = new FutureTask<>(runnable, "默认的返回值");
            new Thread(task2).start();
            String s = task2.get();
            System.out.println(s);
        }
    }


    public static void main(String[] args) {
        ReadWriteExample example = new ReadWriteExample();

        // 启动多个读线程
        for (int i = 0; i < 100; i++) {

            new Thread(() -> example.readData(), "读线程-" + i).start();

            int finalI = i;

            new Thread(() -> example.writeData(finalI), "写线程").start();
        }

    }
}
