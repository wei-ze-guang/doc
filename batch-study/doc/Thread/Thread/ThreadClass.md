# Thread 类
- Thread类里面的native方法  
1. Java 调用 native 方法的本质
- 任何 Java 的实例方法调用 native 实现时，JVM 会自动传递两个隐藏参数：
  - JNIEnv*：指向 JNI 环境的指针，包含 JNI 函数表。
  - jobject：调用该方法的 Java 对象的引用（即 this 指针）。
比如：  
```java
private native void start0();

//  实际上是   JNIEXPORT void JNICALL Java_java_lang_Thread_start0(JNIEnv *env, jobject thisObj);
```


| 方法名                                                               | 实例/类方法 | 功能简述              | 作用与调用时机                                                 |
| ----------------------------------------------------------------- | ------ | ----------------- | ------------------------------------------------------- |
| `private native void start0()`                                    | 实例方法   | 启动本地线程            | `Thread.start()` 时调用，触发 JVM 创建操作系统线程                    |
| `private native void interrupt0()`                                | 实例方法   | 设置线程中断标志，唤醒阻塞线程   | `Thread.interrupt()` 时调用，用于中断目标线程                       |
| `private native boolean isInterrupted(boolean ClearInterrupted)`  | 实例方法   | 查询（并可清除）线程中断状态    | `Thread.isInterrupted()` 和 `Thread.interrupted()` 调用时执行 |
| `private static native void setNativeName(Thread t, String name)` | 类方法    | 设置本地线程名           | `Thread.setName()` 或构造时给线程命名                            |
| `private native void setPriority0(int newPriority)`               | 实例方法   | 设置线程优先级           | `Thread.setPriority()` 调用时执行，影响线程调度                     |
| `private native boolean holdsLock(Object obj)`                    | 实例方法   | 判断当前线程是否持有指定对象锁   | `Thread.holdsLock()` 调用时执行，辅助同步调试                       |
| `private native long currentThread()`                             | 类方法    | 获取当前线程本地线程句柄      | `Thread.currentThread()` 时底层调用，返回线程标识                   |
| `private static native void yield()`                              | 类方法    | 让出当前线程执行权         | `Thread.yield()` 调用时，提示调度器切换线程                          |
| `private native void setDaemon(boolean on)`                       | 实例方法   | 设置线程守护状态          | `Thread.setDaemon()` 调用时设置线程是否为守护线程                     |
| `private native void stop0(Object o)`                             | 实例方法   | 强制终止线程（已废弃，不推荐使用） | `Thread.stop()` 调用时，强制结束线程                              |
## Thread，也就是java层面的状态
| 状态名            | 说明                                                                                                               |
| -------------- |------------------------------------------------------------------------------------------------------------------|
| NEW            | 新建状态，线程对象已创建，但还没调用`start()`方法。                                                                                   |
| RUNNABLE       | 可运行状态，线程正在JVM中执行或者等待CPU时间片。`start()`                                                                             |
| BLOCKED        | 阻塞状态，线程因为等待监视器锁（synchronized锁）而被阻塞。 `Object#wait() Object.wait`                                                  |
| WAITING        | 等待状态，线程无限期等待另一个线程显式通知（如调用`wait()`、`join()`没有超时）。                                                                 |
| TIMED\_WAITING | 超时等待状态，线程等待一定时间后自动返回（如`sleep(time)`、`join(time)`、`wait(time)``LockSupport#parkNanos`,`LockSupport#parkUntil ` ）。 |
| TERMINATED     | 终止状态，线程执行完毕或因异常退出。                                                                                               |      
## Thread的构造函数    下面他run方法执行情况
- Thread类里面有一个Runnable的成员变量  
```java
    /* What will be run. */
    private Runnable target;
```
- 你会发现他能直接使Thread类，但是你不继承重写run方法的话，执行的内容时空
  - 继承他重写他的run方法就可以了
  - 或者传入一个Runnable接口的实现类通过他的构造函数传进去也可以，Thread的target就会变为你的传进去的Runnable接口的实现  看下面的Thread构造函数
```java
    public Thread(Runnable target) {
        this(null, target, "Thread-" + nextThreadNum(), 0);
    }
```
```java
/**
 * 就是为什么需要实现这个接口的原因
 *这个target必须要有一个run方法 
 */
    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }
```  
  - 但是Callable怎么没有了？ Callable需要通过FutureTask实现类，封装才会变为Runnable，因为FutureTask实现了Runnable接口， 这个需要看Future接口实现类FutureTask的实现类,内容再后面
- 
| 构造方法签名                                                                                                 | 参数说明                       | 适用场景                                 |
| ------------------------------------------------------------------------------------------------------ | -------------------------- | ------------------------------------ |
| `Thread()`                                                                                             | 无参数，默认使用当前线程的线程组、默认线程名、无任务 | 创建空线程，手动覆盖 `run()` 方法或通过子类           |
| `Thread(Runnable target)`                                                                              | 指定执行任务（`Runnable`），线程组默认   | 最常用，传入任务对象即可                         |
| `Thread(Runnable target, String name)`                                                                 | 指定任务和线程名                   | 为线程取名字，方便调试                          |
| `Thread(String name)`                                                                                  | 指定线程名                      | 任务为空，一般用于继承 `Thread` 后手动写 `run()`    |
| `Thread(ThreadGroup group, Runnable target)`                                                           | 指定线程组和任务                   | 一般用于线程分组管理（不常见）                      |
| `Thread(ThreadGroup group, Runnable target, String name)`                                              | 指定线程组、任务、线程名               | 更完整的线程构造方式                           |
| `Thread(ThreadGroup group, Runnable target, String name, long stackSize)`                              | 再加一个线程栈大小参数                | 高级用法，JVM 不一定支持 `stackSize`           |
| `Thread(ThreadGroup group, String name)`                                                               | 指定线程组和名字，无任务               | 很少使用，一般自己实现 `run()`                  |
| `Thread(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals)` | JDK 14+ 新增                 | 是否继承父线程的 `ThreadLocal`，更高级的控制手段（不常用） |



###  ，这里私有构造方法1 或者说初始化线程构造器哦，Thread通过构造器链，对不不同方法创建的线程，进行包装，各个创建的线程最后都会经过下面的初始化    
- 这个是一个私有构造方法，我们不能自己使用，这个私有构造方法是因为每一个线程都需要优先级，名字，是守护线程还是用户线程，如果用户创建线程的没写这些一般通过
这里把线程的一些配置设置为父线程的一样的配置，如果用户制定了就使用用户的
```java
    /**
     * Initializes a Thread.
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     * @param acc the AccessControlContext to inherit, or
     *            AccessController.getContext() if null
     * @param inheritThreadLocals if {@code true}, inherit initial values for
     *            inheritable thread-locals from the constructing thread
     */
    @SuppressWarnings("removal")
    private Thread(ThreadGroup g, Runnable target, String name,
                   long stackSize, AccessControlContext acc,
                   boolean inheritThreadLocals) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;

        Thread parent = currentThread();
        SecurityManager security = System.getSecurityManager();
        if (g == null) {
            /* Determine if it's an applet or not */

            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security manager doesn't have a strong opinion
               on the matter, use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(
                        SecurityConstants.SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }

        g.addUnstarted();
        
        this.group = g;
        /**
         * 这个线程组已经不推荐使用了，因为，以前可以可以一次打断一个线程组的每一个线程
         */
        this.daemon = parent.isDaemon();  //先设置为父线程的类型，通过继承父线程的守护线程还是用户线程
        this.priority = parent.getPriority();  //优先级也是一样的  
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;   //类加载器也一样，
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;  //这个就是通过runnable或者callable实现接口的实现类
        setPriority(priority);
        if (inheritThreadLocals && parent.inheritableThreadLocals != null)  //这个东西其实就是可以获取父现成的ThreadLocal的东西
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /**
         * 1. 什么是 InheritableThreadLocal？
         *ThreadLocal 是线程局部变量，每个线程自己保存一份，互不干扰。InheritableThreadLocal 是 ThreadLocal 的一个变种，允许子线程继承父线程的值。
         *这样，子线程启动后，可以访问父线程设置的局部变量，常用于传递用户上下文、事务信息等
         * 2. 这段代码具体做了什么？
         *inheritThreadLocals 是一个布尔参数，表示是否让新线程继承父线程的线程局部变量
         *parent.inheritableThreadLocals 是父线程的 InheritableThreadLocal 变量映射表。
         *通过调用 ThreadLocal.createInheritedMap，复制一份父线程的线程局部变量数据给新线程。
         *这样，新线程一开始就带有父线程的局部变量副本，之后彼此独立。
         * | 概念                       | 说明                          |
         *| ------------------------ | --------------------------- |
         *| `ThreadLocal`            | 线程独享变量                      |
         *| `InheritableThreadLocal` | 子线程继承父线程变量的特殊 `ThreadLocal` |
         *| `inheritThreadLocals` 标志 | 是否让子线程继承父线程局部变量             |
         *| `createInheritedMap()`   | 复制父线程局部变量映射给子线程             |
         */
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        this.tid = nextThreadID();
    }
```
#### 下面构造方法一般我们不用，但是他在Thread里面就是一个构造器传播起到比较大的作用  
```java
/**
 * 这个消费者组一般不用了
 * @param group
 * @param target
 * @param name
 * @param stackSize
 */
   public Thread(ThreadGroup group, Runnable target, String name,
                  long stackSize) {
    /**
     * 下面这个构造商法就是上面的构造方法1
     */
        this(group, target, name, stackSize, null, true);
    }

```  
## 看看一下Callable<T> 如果转为Runnable的  FutureTask包装之后一Callable<T>变为Runnable的
### 看一下FutureTask类 
```java

public class FutureTask<V> implements RunnableFuture<V>
/** The underlying callable; nulled out after running */
private Callable<V> callable; //这个是FutureTask里面的一个一个成员变量
//下面的就成了
public interface RunnableFuture<V> extends Runnable, Future<V> {
    /**
     * Sets this Future to the result of its computation
     * unless it has been cancelled.
     * 下面的方法是Runnable接口那个run方法，这里只是标注和说明
     */
    void run();
}
```
### FutureTask类的构造方法  
- 构造方法一  
```java
/**
 * 如果你传入Callable的我直接把 this.callable = callable;
 * @param callable
 */
    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }
```
- 构造方法二 
```java
/**
 * 这里会传入一个 Runnable runnable,和一个 V result
 * 为什么需要传入  V result  ，因为Runnable是无返回值的，就是如果你的线程执行成功就返回你传入的值，你看到你就会知道线程执行成功了
 * @param runnable
 * @param result
 */
    public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);  //这里就是如何把一个Runnable接口的转为call的关键
        this.state = NEW;       // ensure visibility of callable
    }
    
    /* 看一下 Executors.callable(runnable, result)  方法
        public static <T> Callable<T> callable(Runnable task, T result) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<T>(task, result);  //再看这里
    }
    
      /**
     * A callable that runs given task and returns given result.这里就是Runnable到Callable的适配器
     */
    private static final class RunnableAdapter<T> implements Callable<T> {
        private final Runnable task;
        private final T result;
        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            //关键看这个call方法
            task.run();
            return result;
        }
        public String toString() {
            return super.toString() + "[Wrapped task = " + task + "]";
        }
    }
     */
```
### 先看FutureTask如何实现的Runnable接口的run方法
- FutureTask  有两个属性  
```java
    /** The underlying callable; nulled out after running */ //这里保存FutureTask传入的Runnable或者Callable<T>的实例
    private Callable<V> callable;
    /** The result to return or exception to throw from get() */ //这里保存结果
    private Object outcome; // non-volatile, protected by state reads/writes
```
```java
    //这个就是FutureTask实现的Runnable接口的run方法具体实现
   public void run() {
        if (state != NEW ||
            !RUNNER.compareAndSet(this, null, Thread.currentThread()))
            return;
        try {
            Callable<V> c = callable;   //看这里，如果FutureTask传入的Callable的实例，可以使用Callable的call()方法，如果不是就需要使用适配器把Runnable的run方法弄成call
            if (c != null && state == NEW) {
                V result;
                boolean ran;
                try {
                    /**
                     * 看这里，
                     */
                    result = c.call();
                    ran = true;
                } catch (Throwable ex) {
                    result = null;
                    ran = false;
                    setException(ex);
                }
                if (ran)
                    set(result);
            }
        } finally {
            // runner must be non-null until state is settled to
            // prevent concurrent calls to run()
            runner = null;
            // state must be re-read after nulling runner to prevent
            // leaked interrupts
            int s = state;
            if (s >= INTERRUPTING)
                handlePossibleCancellationInterrupt(s);
        }
    }
```

- 下面是Executors类下面的一个静态类，就是他把
```java
    /**
     * A callable that runs given task and returns given result.
     */
    private static final class RunnableAdapter<T> implements Callable<T> {
        private final Runnable task;
        private final T result;
        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            task.run();
            return result;
        }
        public String toString() {
            return super.toString() + "[Wrapped task = " + task + "]";
        }
    }
```
