### 监视器  
2. 监视器的数据结构保存在何处？
- 对象头（Mark Word）
在 HotSpot JVM 中，每个对象的内存布局中，对象头（Object Header） 包含一个叫 Mark Word 的字段。
Mark Word 存储了对象的锁状态信息：  
是否被锁定  
锁的偏向线程ID  
锁的状态（无锁、偏向锁、轻量级锁、重量级锁）  
还有一些哈希码、GC相关信息等。

- Monitor 结构体（重量级锁时） Monitor 是 JVM 内部的数据结构 它用来管理对象的重量级锁状态和线程同步。 以 C++ 代码实现，驻留在 JVM 进程的本地内存（native heap）中，而不是 Java 堆里。
你在 Java 代码里看不到 Monitor，也不能直接访问它。
如果锁升级为重量级锁，Mark Word 里保存的是指向 Monitor 对象的指针。  
- Monitor 是 JVM 内部的一个C++数据结构，包含：
  - 所持有锁的线程（owner）
  - 等待该锁的线程队列（Entry List）
  - 等待 wait() 的线程队列（Wait Set）  
- 监视器是如何实现线程同步的？
  - 轻量级锁、偏向锁：利用CAS操作和对象头Mark Word，减少线程阻塞，优化性能。
  - 重量级锁（互斥锁）：通过操作系统底层的互斥量（mutex）实现，阻塞其他线程。 
- 监视器如何管理线程的“进入”和“等待”？
1. 进入监视器（synchronized加锁）：
2. 线程尝试获取对象的锁。
3. 如果无锁或锁可用，线程成功持有锁，进入临界区。
4. 否则，线程进入 Entry List 排队等待。
5. 等待监视器（wait()）：
6. 持有锁的线程调用wait()，会释放锁并进入 Wait Set，进入等待状态。
7. 其他线程调用notify()或notifyAll()，将等待线程从 Wait Set 唤醒，重新进入 Entry List 排队竞争锁。  

   | 方面    | 说明                                            |
   | ----- | --------------------------------------------- |
   | 监视器位置 | 对象头的Mark Word，锁升级时指向Monitor结构                 |
   | 锁状态   | 无锁、偏向锁、轻量级锁、重量级锁                              |
   | 数据结构  | Monitor结构（C++），包含owner线程，Entry List，Wait Set等 |
   | 线程管理  | 线程竞争锁进入Entry List，调用wait进入Wait Set            |
   | 查看工具  | JOL、jcmd、jstack、VisualVM                      |  
### Monitor 和 Java 对象的关系
普通情况下（无锁、偏向锁、轻量级锁），锁信息直接存在 Java 对象的头部（Mark Word）里。
当锁升级到重量级锁时，Mark Word 里保存的是一个指向 Monitor 的指针（内存地址）。
- Monitor 负责管理：
1. 当前拥有锁的线程（owner）
2. 等待获取锁的线程队列（Entry List）
3. 调用 wait() 等待的线程队列（Wait Set）  
### 动态变化示意图  
```text
无锁  →  偏向锁  →  轻量级锁  →  重量级锁
 ↑                                   ↓
    <—————— 锁释放或竞争减少 ——————

```  
3. 具体过程举例
1. 线程A进入synchronized块，给对象加偏向锁，Mark Word标记线程ID。
2. 线程B尝试进入，发现偏向锁被占用，撤销偏向锁，升级成轻量级锁，使用CAS竞争。
3. 如果CAS竞争失败（多线程高并发），锁升级为重量级锁，Mark Word指向Monitor对象，线程阻塞等待。
