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