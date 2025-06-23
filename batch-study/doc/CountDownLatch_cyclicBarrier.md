| 特性对比    | `CountDownLatch`        | `CyclicBarrier`                |
| ------- |-------------------------| ------------------------------ |
| 初始作用    | **等待“事件发生次数”**          | **等待“线程数量”**                   |
| 典型用途    | 等待一组任务执行完后再继续           | 多线程“集合点”后再一起前进                 |
| 能否重复使用？ | ❌ 只用一次，重复使用就要重新创建       | ✅ 可多次循环使用                      |
| 实现机制    | AQS（共享模式）               | 基于 `ReentrantLock + Condition` |
| 等待谁/什么？ | 主线程等待其他线程 `countDown()` | 所有线程相互等待（都必须到达）                |  
---  


## ✅ CountDownLatch 场景：等其他线程干完事，private volatile int state;这个数量用来统计的，构造方法传入
```java
CountDownLatch latch = new CountDownLatch(3);  //state初始化为1

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        doWork();
        latch.countDown(); // 通知主线程：我干完了,state数量减1
    }).start();
}

latch.await(); // 主线程等待所有子线程都完成，等待 state == 0 变成 0 才返回 ，这里会挂起主线程，为0的话就会唤醒
System.out.println("所有任务完成，继续执行主线程");

```  
**🧠 类比：你等 3 个外卖员送餐，送完你就开饭，不管他们谁先到，也不重复**  ---  
CountDownLatch 就是定义一个state，只要这个数量变为0，如果哪一个线程内调用了latch.await();方法，必须等到state  
变为0才会被唤醒，不管顺序，只管数量


## CyclicBarrier 是 Java 并发工具包中一个非常实用的“同步屏障”，它允许一组线程互相等待，直到全部线程都准备就绪，再一起继续执行。它适合分阶段并行、任务协调、多线程同步启动等场景。
🧠 一句话理解：
“等大家都到了再出发”——所有线程必须在屏障前集合，集合满了才一起继续执行！ 通过回调 
| 应用场景              | 示例说明                      |
| ----------------- | ------------------------- |
| 多线程并行计算，汇总结果      | 每个线程计算一部分数据，汇总前需要等待所有线程完成 |
| 多阶段游戏（如副本匹配、组队）   | 所有玩家加载完再统一开始战斗            |
| 模拟接力赛，每一阶段完成后一起出发 | 每个线程表示一个运动员，必须等上一个阶段全部完成  |
| 科研模拟或多阶段流程（如仿真器）  | 每阶段都必须统一步调才能继续            |
---  
```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    public static void main(String[] args) {
        // 设置屏障点为 3，所有线程到达后执行 barrierAction
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            System.out.println("所有线程都准备好了，开始执行任务！");
        });

        for (int i = 1; i <= 3; i++) {
            int threadId = i;
            new Thread(() -> {
                System.out.println("线程 " + threadId + " 正在准备...");
                try {
                    Thread.sleep(1000 * threadId); // 模拟准备时间不同
                    System.out.println("线程 " + threadId + " 准备完毕，等待其他线程...");
                    barrier.await(); // ❗ 等待其他线程
                    System.out.println("线程 " + threadId + " 正在执行任务！");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

```  
CyclicBarrier 底层没有使用 AQS，而是使用了： ReentrantLock + Condition 内部有一个计数器 count，初始为构造时设置的线程数 parties  
每次调用 await()，就将 count-- 如果 count > 0，当前线程阻塞 如果 count == 0，表示最后一个线程到了，所有阻塞线程一起唤醒 然后自动重置计数器为原始值，可再次使用  
这就是它叫 “Cyclic” 的原因：循环可复用  

###  下面是复用的案例
```java
package countbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierReuseDemo {

    static final int parties = 3;
    static  CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
        System.out.println(">>> 所有人到达屏障点，开始下一阶段\n");
    });
    //上面触发之后barrier 会回到原来的状态，可以重复使用，比如上面的又变味了3

    public static void main(String[] args) {

        for (int i = 0; i < parties; i++) {
            new Thread(new Worker(barrier, i)).start();
        }

    }
    static class Worker implements Runnable {

        private final CyclicBarrier cyclicBarrier;

        private final int id;

        public Worker(CyclicBarrier cyclicBarrier, int id) {
            this.cyclicBarrier = cyclicBarrier;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                for (int round = 1; round <= 3; round++) {
                    System.out.println("线程 " + id + " 第 " + round + " 轮准备...");
                    Thread.sleep((long)(Math.random() * 1000)); // 模拟任务
                    System.out.println("线程 " + id + " 第 " + round + " 轮就绪，等待其他人...");
                    barrier.await(); // 每一轮都同步一次 第一次循环调用的时候，就已经卡在这里了，
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}

```  
## 一、什么是 Semaphore？
Semaphore（信号量）是 java.util.concurrent 中的一个用于控制多个线程访问共享资源的并发工具类，底层基于 AQS 实现。  
本质上是一个计数器：控制最多有多少个线程能同时访问某个资源。  
---  
| 构造方法                                   | 说明                                             |
| -------------------------------------- | ---------------------------------------------- |
| `Semaphore(int permits)`               | 创建一个**非公平信号量**，最多允许 `permits` 个线程同时访问资源        |
| `Semaphore(int permits, boolean fair)` | 创建一个信号量，`fair = true` 表示使用 **公平锁** 策略（FIFO 顺序） |
---  
| 方法                                                     | 是否阻塞  | 描述                      |
| ------------------------------------------------------ | ----- |-------------------------|
| `acquire()`                                            | ✅ 是   | 请求一个许可证，没有就阻塞           |
| `acquire(int permits)`                                 | ✅ 是   | 请求多个许可证                 |
| `acquireUninterruptibly()`                             | ✅ 是   | 请求一个许可证，但不可中断           |
| `tryAcquire()`                                         | ❌ 否   | 尝试获取一个许可证，失败就返回 `false` |
| `tryAcquire(int permits)`                              | ❌ 否   | 尝试获取多个许可证               |
| `tryAcquire(long timeout, TimeUnit unit)`              | ⏳ 可阻塞 | 尝试获取一个许可证，等待一段时间        |
| `tryAcquire(int permits, long timeout, TimeUnit unit)` | ⏳ 可阻塞 | 尝试获取多个许可证，带超时           |
| `release()`                                            | ❌ 否   | 释放一个许可证（通知其他线程）,注意下面的   |
| `release(int permits)`                                 | ❌ 否   | 释放多个许可证                 |
| `availablePermits()`                                   | ❌ 否   | 当前可用的许可证数量              |
| `drainPermits()`                                       | ❌ 否   | 一次性获取所有剩余的许可证（变为 0）     |
| `reducePermits(int reduction)`                         | ❌ 否   | 减少指定数量的许可证（慎用）          |
| `isFair()`                                             | ❌ 否   | 是否是公平锁策略                |
| `hasQueuedThreads()`                                   | ❌ 否   | 是否有线程在等待许可              |
---  
| 操作                                  | 是否允许 | 推荐吗                 |
| ----------------------------------- | ---- |---------------------|
| 拿 1 放 1 (`acquire() + release()`)   | ✅    | ✅ 推荐                |
| 拿 2 放 2 (`acquire(2) + release(2)`) | ✅    | ✅ 推荐                |
| 拿 1 放 2 (`acquire() + release(2)`)  | ✅    | ❌ 不推荐（会导致多放）会多出来通行证 |
| 拿 1 放 0 (`acquire() + 不释放`)         | ✅    | ❌ 不推荐（会死锁）          |
---  

- acquire()：底层调用 AQS 的 tryAcquireShared() 方法，尝试扣减许可证数。
- release()：底层调用 AQS 的 releaseShared() 方法，增加许可并唤醒等待线程。
- AQS 维护一个共享计数器，多个线程可以共享通过（不像互斥锁那样只允许一个线程）。  
---  
场景：上厕所模型（限制资源访问）  
```java
Semaphore semaphore = new Semaphore(3); // 模拟 3 个厕所

Runnable person = () -> {
    try {
        semaphore.acquire(); // 请求上厕所（若没坑位，则排队等）
        System.out.println(Thread.currentThread().getName() + " 占用厕所");
        Thread.sleep(3000); // 上厕所中...
        System.out.println(Thread.currentThread().getName() + " 离开厕所");
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        semaphore.release(); // 离开后释放坑位
    }
};

for (int i = 1; i <= 10; i++) {
    new Thread(person, "人" + i).start();
}

```  
## 使用场景
- 限流（Rate Limiting）：控制系统中同时处理的请求数  
- 数据库连接池：限制同时连接的线程数量
- 操作系统资源保护：如 I/O 限制、线程池容量控制
- 高并发限速：允许一定并发线程，其余等待许可
- 读写限量访问：比如限制只有 5 个线程能读取某个文件

