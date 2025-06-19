package lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteExample {
    private int data = 0; // 要保护的数据
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final ReentrantLock lock = new ReentrantLock();

    // 读操作
    public int readData() {
        rwLock.readLock().lock(); // 获取读锁
        try {
            System.out.println(Thread.currentThread().getName() + " 正在读取数据: " + data);
            Thread.sleep(10); // 模拟读操作
            return data;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock(); // 释放读锁
        }
    }

    // 写操作
    public void writeData(int value) {
        rwLock.writeLock().lock(); // 获取写锁
        try {
            System.out.println(Thread.currentThread().getName() + " 正在写入数据: " + value);
            Thread.sleep(100); // 模拟写操作
            data = value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock(); // 释放写锁
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
