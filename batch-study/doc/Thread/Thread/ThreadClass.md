# Thread 类
- Thread类里面的native方法  
- 
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

-  下面是源码
```java

    public enum State {
        /**
         * Thread state for a thread which has not yet started.
         */
        NEW,

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
        RUNNABLE,

        /**
         * Thread state for a thread blocked waiting for a monitor lock.
         * A thread in the blocked state is waiting for a monitor lock
         * to enter a synchronized block/method or
         * reenter a synchronized block/method after calling
         * {@link Object#wait() Object.wait}.
         */
        BLOCKED,

        /**
         * Thread state for a waiting thread.
         * A thread is in the waiting state due to calling one of the
         * following methods:
         * <ul>
         *   <li>{@link Object#wait() Object.wait} with no timeout</li>
         *   <li>{@link #join() Thread.join} with no timeout</li>
         *   <li>{@link LockSupport#park() LockSupport.park}</li>
         * </ul>
         *
         * <p>A thread in the waiting state is waiting for another thread to
         * perform a particular action.
         *
         * For example, a thread that has called {@code Object.wait()}
         * on an object is waiting for another thread to call
         * {@code Object.notify()} or {@code Object.notifyAll()} on
         * that object. A thread that has called {@code Thread.join()}
         * is waiting for a specified thread to terminate.
         */
        WAITING,

        /**
         * Thread state for a waiting thread with a specified waiting time.
         * A thread is in the timed waiting state due to calling one of
         * the following methods with a specified positive waiting time:
         * <ul>
         *   <li>{@link #sleep Thread.sleep}</li>
         *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
         *   <li>{@link #join(long) Thread.join} with timeout</li>
         *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
         *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
         * </ul>
         */
        TIMED_WAITING,

        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         */
        TERMINATED;
    }
```  
###  ，这里成为构造方法1或者说初始化线程构造器哦，Thread通过构造器链，对不不同方法创建的线程，进行包装，各个创建的线程最后都会经过下面的初始化    
- 这个是一个私有构造方法，我们不能自己使用
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
#### Thread 内他自己实现的Runnable的方法  
```java
/**
 * 就是为什么需要实现这个接口的原因
 */
    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }
```