## 和打断相关的    
### Thread的变量interrupted，这个变量是由JVM自己操作的，用户无法自己操作  ,当不同的方法调用的时候这个变量会发生变化
- 这个变量有什么作用呢？这个非常重要，用来保存这个线程是否被打断过，是实例线程，并不是当前线程，当然，如果实例线程他自己想要操作这个
这个变量的变化也是可以的，想要这个变量的的值改变，我们只能通过方法，不能直接给他赋值，
```java
  /* Interrupt state of the thread - read/written directly by JVM */
  private volatile boolean interrupted;
```

注意这里  
```java
   /**
     * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds, subject to
     * the precision and accuracy of system timers and schedulers. The thread
     * does not lose ownership of any monitors.
     *
     * @param  millis
     *         the length of time to sleep in milliseconds
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    public static native void sleep(long millis) throws InterruptedException;

    /**
     * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds plus the specified
     * number of nanoseconds, subject to the precision and accuracy of system
     * timers and schedulers. The thread does not lose ownership of any
     * monitors.
     *
     * @param  millis
     *         the length of time to sleep in milliseconds
     *
     * @param  nanos
     *         {@code 0-999999} additional nanoseconds to sleep
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative, or the value of
     *          {@code nanos} is not in the range {@code 0-999999}
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    public static void sleep(long millis, int nanos)
    throws InterruptedException {
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos > 0 && millis < Long.MAX_VALUE) {
            millis++;
        }

        sleep(millis);
    }
```  
---  
### isInterrupted(）方法，他是对象的方法，这个方法唯一作用就是查询这个对象线程的interrupted的值，只查询，不做任何操作
```java
   public boolean isInterrupted() {
        return interrupted;
    }
```  
---  
### public static boolean interrupted() 方法，是一个静态方法 他会查询检查当前线程当前线程状态和改变，根据interrupted这个变量，注意名字哈，这个是上面的变量
- 这个方法和上面的isInterrupted(）作用是什么呢，他也是查询，但是是一个静态方法，返回的永远也是这个变量interrupted的值，这个作用跟上面一样  
- 但是public static boolean interrupted() 会先查询变量interrupted的值  
  - 如果发现是false，那么啥也不管了，直接返回变量interrupted的值false
  - 如果是true，一样返回true，但是我会把变量interrupted的值改为false
- 说人话就是，有人叫我去查询一下 变量interrupted的值是什么，不管你查询到他的值是什么你够给我返回他，但是如果他是true，麻烦你帮我把他修改为false
```java
/**
 * 检查当前线程是否已被中断。
 *
 * 该方法会**清除当前线程的中断状态**。
 * 换句话说，如果连续调用两次该方法，第一次会返回 true 并清除中断标志；
 * 如果在第一次和第二次之间没有再次被中断，那么第二次将返回 false。
 *
 * @return 如果当前线程已被中断，返回 true；否则返回 false。
 * @see #isInterrupted()
 */
public static boolean interrupted() {
    Thread t = currentThread();
    // 获取当前正在执行的线程对象

    boolean interrupted = t.interrupted;
    // 读取当前线程的中断标志（true 表示线程被打断过）

    // 这段注释的意思是：
    // “可能我们刚读取中断标志后，线程又被打断了”
    // 所以我们只在标志是 true 的时候才清除它，否则可能丢失中断信号。
    if (interrupted) {
        t.interrupted = false;
        // 清除中断标志：这里是这整个方法最重要的副作用
        clearInterruptEvent();
        // 清理与中断相关的系统状态（底层 native 操作）
    }

    return interrupted;
    // 返回原来中断标志的值
}

```  
### 重磅方法    public void interrupt()，实例方法
```java
    /**
     * Interrupts this thread.
     *
     * <p> Unless the current thread is interrupting itself, which is
     * always permitted, the {@link #checkAccess() checkAccess} method
     * of this thread is invoked, which may cause a {@link
     * SecurityException} to be thrown.
     *
     * <p> If this thread is blocked in an invocation of the {@link
     * Object#wait() wait()}, {@link Object#wait(long) wait(long)}, or {@link
     * Object#wait(long, int) wait(long, int)} methods of the {@link Object}
     * class, or of the {@link #join()}, {@link #join(long)}, {@link
     * #join(long, int)}, {@link #sleep(long)}, or {@link #sleep(long, int)}
     * methods of this class, then its interrupt status will be cleared and it
     * will receive an {@link InterruptedException}.
     * 如果当前线程正在阻塞于以下方法：

     *  Object.wait()、Object.wait(long)、Object.wait(long, int)
     * Thread.join()、Thread.join(long)、Thread.join(long, int)
     * Thread.sleep(long)、Thread.sleep(long, int)
     * 那么：
     * 中断状态将被清除（interrupted = false）
     * 并且线程将会收到一个 InterruptedException 异常
     * ✅ 更详细一点的解释：
     *当线程处于阻塞状态（sleep / wait / join 等）时，如果其他线程调用 .interrupt()：
     *JVM 记录下“你被打断了”（设置中断标志）
     *然后 JVM 唤醒这个线程
     *接着 JVM 负责清除中断标志
     *然后抛出 InterruptedException 给这个线程
         *
     * <p> If this thread is blocked in an I/O operation upon an {@link
     * java.nio.channels.InterruptibleChannel InterruptibleChannel}
     * then the channel will be closed, the thread's interrupt
     * status will be set, and the thread will receive a {@link
     * java.nio.channels.ClosedByInterruptException}.
     *
     * <p> If this thread is blocked in a {@link java.nio.channels.Selector}
     * then the thread's interrupt status will be set and it will return
     * immediately from the selection operation, possibly with a non-zero
     * value, just as if the selector's {@link
     * java.nio.channels.Selector#wakeup wakeup} method were invoked.
     *
     * <p> If none of the previous conditions hold then this thread's interrupt
     * status will be set. </p>
     *
     * <p> Interrupting a thread that is not alive need not have any effect.
     *
     * @implNote In the JDK Reference Implementation, interruption of a thread
     * that is not alive still records that the interrupt request was made and
     * will report it via {@link #interrupted} and {@link #isInterrupted()}.
     *
     * @throws  SecurityException
     *          if the current thread cannot modify this thread
     *
     * @revised 6.0, 14
     */
    public void interrupt() {
        if (this != Thread.currentThread()) {
            checkAccess();

            // thread may be blocked in an I/O operation
          /**
           * blocker 是 Thread 类中的一个字段，用来记录这个线程当前是否被某个 I/O 操作阻塞。
           *如果 blocker != null，说明当前线程正在执行一个支持中断的 I/O 操作，比如 NIO 的通道操作。
           *b.interrupt(this) 就是告诉这个阻塞操作：我要中断这个线程了，你自己来处理如何中断。
           */
            synchronized (blockerLock) {
                Interruptible b = blocker;
                if (b != null) {
                    interrupted = true;
                    interrupt0();  // inform VM of interrupt
                    b.interrupt(this);
                    return;
                }
            }
        }
        interrupted = true;
        // inform VM of interrupt
        interrupt0();
    }
```  
public void interrupt()  
- 这个方法一定会把被打断的的线程的打断状态标志设置围殴true，就是上面的关键字设置为true
- 可以自己打断自己，可以别别人打断，两个唯一区别就是如果自己打断自己的话，不会主动去打断自己的IO，被别人打断这个IO也会被打断，但是都不是强势打断，如果自己在什么sleep，wait等，自己打断自己也是会抛出异常的
### 重点总结  
- 如果一个线程被别人打断，他不会立刻知道，除非他自己去查询  
- JVM会看到，某个线程被中断了  
  - 如果这个线程不在什么sleep，wait，join等状态，或者IO(这个不是自己打断自己才有),就直接给被打断哪个线程中断标志设置为true，什么都不干了,这是JVM自己做的
  - 如果上面处于sleep，wait，join等状态，或者IO(这个不是自己打断自己才有),状态，JVM，然后会先清除他的标志，然后再给线程抛出打断的异常，这个时候就需要编码的时候注意了  
### 总结
✅ 情况一：线程正在执行代码，没有阻塞  
比如这个线程在死循环：
```java
while (!Thread.currentThread().isInterrupted()) {
// 正在忙着干活，没调用任何阻塞方法
}
```
这时候别的线程调用 t.interrupt()
✅ 它的 中断标志会被置为 true ❌ 但线程本身并不会立刻“醒”或者“停下来” 要靠线程自己在循环里检查中断标志，再做决定  
✅ 情况二：线程处于阻塞状态，比如：  
- Thread.sleep()
- Object.wait()
- Thread.join()
- LockSupport.park()
- BlockingQueue.take()
- Condition.await()
当你调用 t.interrupt()：
- ✅ JVM 会立即唤醒这个线程
- ✅ 抛出 InterruptedException
- ✅ 清除中断标志（变成 false）
- 所以这时候，线程确实是“立即被激活”的！  
✅ 特例：LockSupport.park()
- 这个方法不会抛出异常，但中断会使其立即返回，并保留中断标志。

