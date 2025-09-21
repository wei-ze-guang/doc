| 特性                | 含义                          | 实现方式（Java）                                    |
| ----------------- | --------------------------- | --------------------------------------------- |
| ✅ 可见性（Visibility） | 一个线程修改了共享变量，其他线程能“立刻看到”     | `volatile`、`synchronized`、`final`             |
| ✅ 原子性（Atomicity）  | 操作过程不能被中断或切分，多个线程不能“同时修改成功” | `synchronized`、`Lock`、`AtomicInteger` 等原子类    |
| ✅ 有序性（Ordering）   | 程序执行的顺序和代码写的顺序一致            | `volatile`、`synchronized`、`happens-before` 规则 |

并发三要素：V-A-O
V：可见性（volatile）
A：原子性（Atomic、锁）
O：有序性（happens-before）


### 1️⃣ 可见性：多线程之间看到一致的数据
问题来源：每个线程有自己的工作内存（CPU缓存），共享变量的修改可能不会立刻反映给其他线程。

解决方法  

| 技术             | 实现方式                     |
| -------------- | ------------------------ |
| `volatile`     | 写操作后立即刷新到主内存，读操作强制从主内存读取 |
| `synchronized` | 解锁时会刷新主内存，锁住时清除工作内存，保证一致 |
| `final`        | 在构造函数中正确赋值后，保证其他线程能看到该值  |
#### 使用volatile处理可见性
1. 可见性的理解，一般来说线程会在线程执行完之后把数据刷新回内存，volatile解决了这个问题是当线程操作后他会立刻刷新回内存，   
重要的是这个立刻，当线程执行完之后， 他可能还会继续执行别的代码，那么这个时候他不管  他先先刷新内存再执行别的代码
2.  下面代码如果不使用volatile的话main线程执行完之后，他才会刷新回内存，但是使用了这个之后，main线程对 ready = true;其他线程  
能够立刻看到，子线程看到 ready=true，继续执行！会比 主线程还在干别的事：打印的早  
3. volatile 主要是是立刻！立刻！立刻！volatile还使用内存屏障防止重排序，但是他不能解决原子性和顺序性问题  
4. 总结，valatile就是只解决了内存可见性，别的没有解决
```java
public class VolatileVisibleDemo {

    // volatile 保证可见性
    private static volatile boolean ready = false;

    public static void main(String[] args) throws InterruptedException {

        // 启动线程读取 ready
        Thread reader = new Thread(() -> {
            while (!ready) {
                // 等待 ready 为 true
            }
            System.out.println("子线程看到 ready=true，继续执行！");
        });

        reader.start();

        // 主线程睡一会儿
        Thread.sleep(1000);

        // 写 volatile 变量
        ready = true;

        // 🔽 即使还有后续操作，ready=true 也已经对 reader 可见
        System.out.println("主线程：我设置好了 ready=true");

        // 后续操作仍然照常进行
        for (int i = 0; i < 3; i++) {
            System.out.println("主线程还在干别的事：" + i);
        }
    }
}

```

#### 使用final  ,他的作用是在对象实行玩构造函数之后被final修饰的变量一定是赋值的，并且线程可见
1. 对象构造函数执行完之后，final 修饰的字段一定完成了赋值，且赋的值对其他线程是可见的，不会出现“半初始化”状态。
2.  什么是半初始化 ,半初始化就是使用了n构造函数之后对象引用已经完成，但是函数属性还没赋值，一个线程new了这个Demo类的对象，  
使用final就相当于默认构造函数之后这个属性初始化完成
```java
//如果没有 final，假设线程A创建了一个 Demo 对象，  
// 但由于重排序，线程B在对象完全构造好之前看到的 x 可能是默认值0，地址引用已经生效（“半初始化”状态）。但使用 final，JVM保证了线程B看到的是构造函数里设置的那个正确值。
//因为重排序机制 
public class Demo {
    final int x;

    public Demo(int x) {
        this.x = x;  // final 字段赋值
        // 其他复杂初始化操作...
    }
}

```  
```java
//打算这样使用他
Demo obj = new Demo(42);
// 把 obj 共享给其他线程

```  
3. 为什么会出问题（没有 final 的情况）   
Java 中 对象的构造 = 多个步骤的组合，大致是这样： 分配内存 -> 调用构造方法 ->  给字段赋值 -> 执行初始化逻辑 -> 返回引用  
但是CPU和JVM会为了性能，会重新排序,下面的
   可能会发生这样的顺序（没有 final 的话）：
-----------------------------------------------------
线程A：
1. 分配内存 -> OK
2. 把 obj 引用写入共享变量（比如全局变量）！！（还没初始化完！）  
//这里就是地址已经别抛出去了，但是x还没完成初始化,如果这个obj被i引用，别人就会看到，但是x还没完成
3. 初始化 x = 42（延后）

线程B：
1. 看到 obj != null，就去读 obj.x
2. 结果 obj.x = 默认值 0（因为初始化还没执行）

😱 —— 这就出现了“半初始化”现象

但是对 final 字段的写入，JVM 会禁止某些重排序，确保： 对象构造中对 final 字段的赋值不会被重排序到构造函数之外。 这样可以避免其他线程看到部分构造完成的对象。



