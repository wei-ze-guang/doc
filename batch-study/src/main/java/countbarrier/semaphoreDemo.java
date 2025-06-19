package countbarrier;

import java.util.concurrent.Semaphore;

/**
 * ---
 * | 方法                                                     | 是否阻塞  | 描述                      |
 * | ------------------------------------------------------ | ----- |-------------------------|
 * | `acquire()`                                            | ✅ 是   | 请求一个许可证，没有就阻塞           |
 * | `acquire(int permits)`                                 | ✅ 是   | 请求多个许可证                 |
 * | `acquireUninterruptibly()`                             | ✅ 是   | 请求一个许可证，但不可中断           |
 * | `tryAcquire()`                                         | ❌ 否   | 尝试获取一个许可证，失败就返回 `false` |
 * | `tryAcquire(int permits)`                              | ❌ 否   | 尝试获取多个许可证               |
 * | `tryAcquire(long timeout, TimeUnit unit)`              | ⏳ 可阻塞 | 尝试获取一个许可证，等待一段时间        |
 * | `tryAcquire(int permits, long timeout, TimeUnit unit)` | ⏳ 可阻塞 | 尝试获取多个许可证，带超时           |
 * | `release()`                                            | ❌ 否   | 释放一个许可证（通知其他线程）,注意下面的   |
 * | `release(int permits)`                                 | ❌ 否   | 释放多个许可证                 |
 * | `availablePermits()`                                   | ❌ 否   | 当前可用的许可证数量              |
 * | `drainPermits()`                                       | ❌ 否   | 一次性获取所有剩余的许可证（变为 0）     |
 * | `reducePermits(int reduction)`                         | ❌ 否   | 减少指定数量的许可证（慎用）          |
 * | `isFair()`                                             | ❌ 否   | 是否是公平锁策略                |
 * | `hasQueuedThreads()`                                   | ❌ 否   | 是否有线程在等待许可              |
 * ---
 * | 操作                                  | 是否允许 | 推荐吗                 |
 * | ----------------------------------- | ---- |---------------------|
 * | 拿 1 放 1 (`acquire() + release()`)   | ✅    | ✅ 推荐                |
 * | 拿 2 放 2 (`acquire(2) + release(2)`) | ✅    | ✅ 推荐                |
 * | 拿 1 放 2 (`acquire() + release(2)`)  | ✅    | ❌ 不推荐（会导致多放）会多出来通行证 |
 * | 拿 1 放 0 (`acquire() + 不释放`)         | ✅    | ❌ 不推荐（会死锁）          |
 * ---
 */

public class semaphoreDemo {

    /**
     *
     */
    private static final Semaphore semaphore = new Semaphore(2000000);

    private static final int threads = 100000; //线程数


    public static void main(String[] args) {

        for (int i = 0; i < threads; i++) {
            new SemaphoreRequire("线程没名字"+i).start();
            new SemaphoreTryRequire("线程名字"+i).start();
        }


    }

    static  class SemaphoreTryRequire extends Thread {

        public SemaphoreTryRequire(String name) {
            super(name);
        }

        @Override
        public void run() {

            boolean b = semaphore.tryAcquire(1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (b) {
                System.out.println("尝试不堵塞拿到信号");
                semaphore.release(1);
            }
            else
                System.out.println("尝试一次没拿到数据但是继续执行");

        }
    }
    static  class SemaphoreRequire extends Thread {

        public SemaphoreRequire(String name) {
            super(name); // 设置线程名字
        }
        @Override
        public void run() {

            boolean b = false;
            try {
                Thread.sleep(100);
                semaphore.acquire();  //会堵塞,取一个
                System.out.println("堵塞拿到信号");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                semaphore.release();  //释放一个
            }

        }
    }
}
