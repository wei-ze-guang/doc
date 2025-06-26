# Future接口及其衍生接口和实现类  
- FutureFuture实现类只能传RunnableFuture的实现类，FutureTask的实现类，ScheduledFutureTask的实现类，CompletableFuture的实现类，ForkJoinTask的实现类。  
如果你继承Thread类，他只会运行run方法，不会返回结果。  
```java
Future（接口） java.util.concurrent
├── RunnableFuture（接口） extends Runnable, Future
│   └── FutureTask（类） 实现了 RunnableFuture，最常用的 Future 实现类
│
├── ScheduledFuture（接口） extends Delayed, Future
│   └── RunnableScheduledFuture（接口） extends RunnableFuture, ScheduledFuture
│       └── ScheduledThreadPoolExecutor.ScheduledFutureTask（内部类）
│
├── CompletableFuture（类） 实现了 Future 和 CompletionStage（用于响应式链式编程）
│
└── ForkJoinTask<V>（抽象类）实现了 Future<V>
├── RecursiveTask<V>（类）用于有返回值的任务
└── RecursiveAction（类）用于无返回值的任务

``` 

## FutureFuture实现类
- 源代码 ,这个源代码你不看也可以
```java


package java.util.concurrent;

/**
 * {@code Future} 表示异步计算的结果。提供了检查计算是否完成、等待其完成以及检索计算结果的方法。
 * 只有在计算完成之后，才能通过 {@code get} 方法检索结果，必要时会阻塞直到结果准备好。
 * 取消操作通过 {@code cancel} 方法执行。额外的方法提供了确定任务是正常完成还是被取消的功能。
 * 一旦计算完成，就无法再取消计算。
 * 如果你希望使用 {@code Future} 来实现可取消性但不需要提供可用的结果，你可以声明类型为 {@code Future<?>}
 * 并返回 {@code null} 作为底层任务的结果。
 *
 * <p><b>使用示例</b> （请注意，以下类都是虚构的。）
 *
 * {@link FutureTask} 类是 {@code Future} 的一个实现，它实现了 {@code Runnable}，
 * 因此可以由 {@code Executor} 执行。例如，上面的 {@code submit} 构造可以被替换为：
 * <pre> {@code
 * FutureTask<String> future = new FutureTask<>(task);
 * executor.execute(future);}</pre>
 *
 * <p>内存一致性效果：异步计算的动作
 * <a href="package-summary.html#MemoryVisibility"> <i>先行发生于</i></a>
 * 另一个线程中跟随相应的 {@code Future.get()} 的动作。
 *
 * @see FutureTask
 * @see Executor
 * @since 1.5
 * @author Doug Lea
 * @param <V> 这个 Future 的 {@code get} 方法返回的结果类型
 */
public interface Future<V> {

    /**
     * 尝试取消此任务的执行。如果任务已经完成或已取消，或者由于其他原因无法取消，
     * 则此方法无效。否则，如果在调用 {@code cancel} 时任务尚未开始执行，
     * 则该任务应永远不会运行。如果任务已经开始执行，
     * 那么 {@code mayInterruptIfRunning} 参数决定了是否尝试中断正在执行此任务的线程
     * （如果实现中已知该线程）。
     *
     * <p>从此方法返回的值不一定表明任务是否已被取消；
     * 使用 {@link #isCancelled}。
     *
     * @param mayInterruptIfRunning 如果为 {@code true}，并且实现了任务执行线程的信息，则中断正在执行此任务的线程；
     * 否则，允许正在进行的任务完成
     * @return 如果任务由于已完成而无法被取消，则返回 {@code false}；
     * 否则返回 {@code true}。如果两个或多个线程导致任务被取消，
     * 则至少其中一个线程返回 {@code true}。实现可能提供更强的保证。
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 如果此任务在正常完成之前被取消，则返回 {@code true}。
     *
     * @return 如果此任务在正常完成之前被取消，则返回 {@code true}
     */
    boolean isCancelled();

    /**
     * 返回 {@code true} 如果此任务已完成。
     * 正常终止、异常或取消都可能导致任务完成——在所有这些情况下，此方法都会返回 {@code true}。
     * @return 如果此任务已完成，则返回 {@code true}
     */
    boolean isDone();

    /**
     * 如果必要，等待计算完成，然后检索其结果。
     *
     * @return 计算结果
     * @throws CancellationException 如果计算被取消
     * @throws ExecutionException 如果计算抛出了异常
     * @throws InterruptedException 如果当前线程在等待时被中断
     */
    V get() throws InterruptedException, ExecutionException;

    /**
     * 如果必要，等待最多给定的时间，直到计算完成，然后检索其结果（如果可用）。
     *
     * @param timeout 最大等待时间
     * @param unit timeout 参数的时间单位
     * @return 计算结果
     * @throws CancellationException 如果计算被取消
     * @throws ExecutionException 如果计算抛出了异常
     * @throws InterruptedException 如果当前线程在等待时被中断
     * @throws TimeoutException 如果等待超时
     */
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}

```  
### 先将一个重点，这个FutureTask实现类他是包装一个线程的，我们知道我们创建线程有三种方法，哪三种你们应该知道，如果你连怎么创建线程还不知道的话，你需要先学习，不是我在这里不教你，是因为一次教你太多会乱的  
- 看FutureTask的构造方法  
  - 构造方法一
    - 他传进去的是一个 Runnable实现类的线程，这种方法创建的线程的话他本身是没有结果的，
    - 这个有什么影响呢，我们获取线程结果的时候使用的是get()方法，但是Runnable是没有结果的
    - 他做了这样子一个构造方法，里面传进去一个 V result 泛型参数，如果线程顺利完成了就返回你传进去的这个参数，就是你在构造方法传进去啥线程顺利的话我就给你返回啥 
```java
    public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;       // ensure visibility of callable
    }
```
 - 构造方法一演示代码  
```java
    static public void RunnableDemo() {
        Runnable runnable = () -> System.out.println("Runnable task running...");
        FutureTask<String> task = new FutureTask<>(runnable, "任务执行完毕"); //注意看这里
        new Thread(task).start();
        String result = null;
        try {
            result = task.get();  // 阻塞，直到任务执行完
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结果：" + result);

    }  //Runnable task running...  结果：任务执行完毕        //注意看这里的输出结果

```
  - 构造方法二  
    - 你只能传进去一个 Callable 接口实现类的线程，这个线程返回啥，等下你传入的线程我能够顺利完成的话，你线程里面你写的返回什么就给你返回什么
```java
    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }
```  
  - 构造方法二演示  
```java
    static public void CallableDemo() {
        String res = "这是动态返回结果";   //注意看这里
        Callable<String> callable = () -> {return res;};   //注意看这里
        FutureTask<String> task = new FutureTask<>(callable );
        new Thread(task).start();
        String result = null;
        try {
            result = task.get();  // 阻塞，直到任务执行完
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结果：" + result);  //注意看这里
        // 输出 `结果：这是动态返回结果`

    }
```
- 这里想过一个问题没有，还有一种实现线程的方法就是继承Thread类，这里为什么没有呢，因为FutureTask无法处理你这个，至于为什么，这里不谈，你自己去查看  
---  
### 下面介绍他的各种方法
#### finishCompletion()方法  
这个方法就是通知哪些所有的调用了get()方法的进程，唤醒他们，无论结果是什么
```java
private void finishCompletion() {
    // 先清理任务执行线程引用，帮助GC
    runner = null;

    // 唤醒所有在等待队列中阻塞的线程（调用get()时进入等待）
    WaitNode q;
    while ((q = waiters) != null) {
        if (UNSAFE.compareAndSwapObject(this, waitersOffset, q, null)) {
            for (;;) {
                Thread t = q.thread;
                if (t != null) {
                    q.thread = null;
                    LockSupport.unpark(t); // 唤醒线程
                }
                WaitNode next = q.next;
                if (next == null)
                    break;
                q = next;
            }
            break;
        }
    }

    // 执行额外的回调钩子，默认空实现，子类可以重写
    done();
}

```
#### cancel(boolean b)方法  这个方法是有参数的
  - 这个方法名字就看出来，只是想取消线程，不是暴力打断，意思就是跟这个线程说我想取消你，我告诉你了，你怎么做你自己看吧
  - 我们来看返回结果吧，这个才是你们学习的重点吧，在讲这个之前，必须必须想清楚这个方法他想做的事情。就是上面的，线程！我想取消你 
    - 这个方法不一定只会被调用一次吧?是不是兄弟们？万一很多线程同时调用这个方法呢？对不对，下面来讲  
      - 当这个方法第一次被调用的使用，无论你的传入的参数是什么，是true还是false，不影响结果，这个后面讨论
        - 只有这个线程还没开始运行的时候他返回true，什么意思呢，就是这个线程在你代码里面你已经调用start方法或者call方法了，但是这个时候不一定的开始运行哦，什么时候开始运行不确定！  
    但是他能判断到底是否已经运行了，说人话就是，你调用cancel()方法，你想让我告诉这个线程我想取消你，然后呢我去看一下这个线程他开始了没有，如果开始了，我就返回false，为什么返回false，因为他已经运行了，我无法取消，如果他还没开始，我就返回true，因为他还没开始呢，我能控制他，帮你取消他。再说人话就是线程如果他已经开始了，我无法控制，我不能帮你取消，我给你返回false  
     - 到这里还是在讨论他第一次运行哈，主要讨论你传入的参数是true还是false  
       - 如果传入true ，意思就是说，我调用cancel(true), 如果你不能打断他的话，你帮我给线程绍说一句话，线程啊，我想取消你，就是调用线程的.interrupt()方法
这个方法不是暴力打断那个线程哈，这个方法也不是FutureTask的方法，而是FutureTask他包装的那个线程实例，那个线程实例有这个方法
       - 如果你传入false，就是你这样子调用cancel(false),就是说你帮我看一下能不能打断那个线程，能打断他，不能就算了，你不用在做其他事情了  
     - 上面讨论完了这个方法被第一次调用，现在讨论这个方法第2，3，4.。。次调用  
     - 先说结果，只要不是第第一次调用，结果返回的都是false，机制是这样的，这个线程只能被允许一次申请取消，不能多次取消，你要取消可以，我都给你返回false，我什么都不做就给你返回一个false  
     - 再说总结就是，这个线程到底是不是被取消了，是不是被取消只在第一次调用有效，第一次调用返回的结果就是这个线程在整个运行期间内有没有被取消过成功！
```java
public boolean cancel(boolean mayInterruptIfRunning) {
    // state：任务当前状态，int型字段，表示任务生命周期阶段
    // NEW：初始状态，任务尚未开始执行
    // INTERRUPTING：正在处理中断请求
    // CANCELLED：任务被取消，且不需要中断线程
    // INTERRUPTED：任务被取消，且线程已被中断
    // stateOffset：是UNSAFE操作时state字段的内存偏移量，保证CAS操作能找到正确位置
    // UNSAFE：用于底层原子操作的工具类（sun.misc.Unsafe），支持无锁并发原子操作

    if (!(state == NEW && 
          // 通过CAS尝试将状态从NEW切换为INTERRUPTING或CANCELLED
          // 这里CAS是“Compare And Swap”的缩写，用于无锁的线程安全状态切换
          UNSAFE.compareAndSwapInt(this, stateOffset, NEW, mayInterruptIfRunning ? INTERRUPTING : CANCELLED)))
        return false;  // 状态不是NEW，或者CAS失败（别人先抢先改了），取消失败

    try {
        if (mayInterruptIfRunning) {  // 是否允许中断正在执行任务
            Thread t = runner;         // runner字段是当前执行任务的线程
            if (t != null) {
                t.interrupt();         // 发起线程中断请求，提醒线程自行终止任务
            }
        }
    } finally {
        if (mayInterruptIfRunning) {
            // 使用UNSAFE的putOrderedInt，保证内存屏障效果
            // 状态从INTERRUPTING变为INTERRUPTED，标识中断流程已完成
            UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
        }
        // 结束取消流程，唤醒所有调用get()等待结果的线程，避免死锁阻塞
        finishCompletion();
    }

    return true;  // 任务成功取消，调用者可据此判断
}

```  
---  
#### boolean isCancelled(),没有参数  
  - 他的返回结果是，这个结果其实就是上面使用cancel的结果很大关系
    - 如果cancel()返回true，这里也返回true，cancel（）返回false就是false
    - 怎么理解呢，这个 sCancelled()  就是查询一下，有没有人尝试过取消这个线程
      - 如果有人尝试过取消，就返回那个人尝试取消的人，他尝试取消的结果是什么，你告诉我一下，这个是查询，他没有动作行为  
      - 如果没有尝试取消过这个线程，那么结果肯定返回false，因为都没有尝试取消他，他哪来的取消啊！！！
```java
//源码
public boolean isCancelled() {
        return state >= CANCELLED;
    }
```  
---  
- boolean isDone()方法，这个方法就是看一下有没有完成 
  - 什么情况下没完成呢，就是你还没开始，肯定没完成，那还在运行，那你还没完成，返回false  
  - 什么情况下完成了呢，他被别人取消了，并且取消成功了，还有就是线程已经运行完毕了，就是完成了  
    - 什么是运行完毕呢？我不管线程是爆粗了，挂壁了还是什么，还有就是已经顺利执行完了，结果返回了，就是运行完毕  
  - 这个方法有什么用呢，直接使用get阻塞不就知道完成没完成了吗，因为完成之后肯定会返回结果啊，
  - isDone()  不是堵塞方法，查一下马上就出结果了，get方法是堵塞的
```java
 public boolean isDone() {
        return state != NEW;
    }
```  
---  
#### V report(int s) 方法，  
  - 这个方法没什么，就是在线程已经完成了，判断一下线程的的结果是什么，这个方法里面他没有动作的，就是判断一下应该返回什么结果，或者抛出异常
```java
    private V report(int s) throws ExecutionException {
        Object x = outcome;
        if (s == NORMAL)
            return (V)x;
        if (s >= CANCELLED)
            throw new CancellationException();
        throw new ExecutionException((Throwable)x);
    }
```  
---  
#### public V get() ,无参方法，就是获取线程的执行结果方法，堵塞的，谁调用设个方法堵塞谁的线程，看事情，是谁调用堵塞谁
```java
    public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if (s <= COMPLETING)
            s = awaitDone(false, 0L);
        return report(s);
    }
```  
---  
- public V get(long timeout, TimeUnit unit) 有参方法
 - 他跟上面的 public V get() 方法有什么区别呢，就是说我我在等你线程给我结果，我就等你这个长时间
   - 如果你在这个时间内返回结果给我，我就要你的结果  
   - 如果这个时间内你没结果，你给我抛出一个 throw new TimeoutException(); 异常，我看到这个异常我知道你在期限内没给我结果，然后我自己处理  
你就自行毁灭吧，你有结果我也不管了
```java
    public V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
    if (unit == null)
        throw new NullPointerException();
    int s = state;
    if (s <= COMPLETING &&
            (s = awaitDone(true, unit.toNanos(timeout))) <= COMPLETING)
        throw new TimeoutException();
    return report(s);
}
```  
#### 方法测试案例，不是很全，但是用了比较多了,先测试get() 不设置参数，就是无限期等等结果
```java
   static void testFutureTask(){
        /**
         * 这里有泛型，大家不懂的话还得去学
         * 这个FutureTask就是包装一个线程，他不能主动运行的
         */
        FutureTask<String> task = new FutureTask<>(new testFutureTaskThread());

        new Thread(task).start();  //只有这个才能运行他

        String result = null;

        try {
            boolean cancelled = task.isCancelled();
            System.out.println("看一下有没有被取消过"+cancelled);
            boolean done = task.isDone();
            System.out.println("查看一下看他完成没有,结果是:"+done);
            result= task.get();  //这里使用的是没有参数的，就是一直等 看这里，这里没设置时间
        }
            catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if (result != null) {
                System.out.println("有结果了,结果是:"+result);

                boolean cancel = task.cancel(true);//试一下取消

                System.out.println("取消的结果是:"+cancel);  //这里线程都结束了，肯定取消不了 false
            }
        }


    }

    static class testFutureTaskThread implements Callable<String> {

        @Override
        public String call() throws Exception {

                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    for (int i = 0; i <10; i++){
                        System.out.println("我是线程，我是狗蛋爱JAVA，我在运行呢");
                    }
                }
                System.out.println("我快结束了，准备返回结果");

            return "我是返回的结果";
        }
    }
```  
#### 测试一下超时拿结果，就是get方法里面设置参数，但是比线程结束时间 小
```java
    static void testFutureTask(){
        /**
         * 这里有泛型，大家不懂的话还得去学
         * 这个FutureTask就是包装一个线程，他不能主动运行的
         */
        FutureTask<String> task = new FutureTask<>(new testFutureTaskThread());

        new Thread(task).start();  //只有这个才能运行他

        String result = null;

        try {
            boolean cancelled = task.isCancelled();
            System.out.println("看一下有没有被取消过"+cancelled);
            boolean done = task.isDone();
            System.out.println("查看一下看他完成没有,结果是:"+done);
            result= task.get(500, TimeUnit.MICROSECONDS); //这里肯定不到1秒 看这里，这里设置了时间
        }
            catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            System.out.println("超时了，他还没返回结果");   //看这里，看这里
        } finally {
            if (result != null) {
                System.out.println("有结果了,结果是:"+result);

                boolean cancel = task.cancel(true);//试一下取消

                System.out.println("取消的结果是:"+cancel);  //这里线程都结束了，肯定取消不了 false
            }
        }


    }

    static class testFutureTaskThread implements Callable<String> {

        @Override
        public String call() throws Exception {
                try{
                    Thread.sleep(1000);  //这里至少一秒才会完成，
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    for (int i = 0; i <10; i++){
                        System.out.println("我是线程，我是狗蛋爱JAVA，我在运行呢");
                    }
                }
                System.out.println("我快结束了，准备返回结果");

            return "我是返回的结果";
        }
    }
    //  下面是输出
//看一下有没有被取消过false
//查看一下看他完成没有,结果是:false
//超时了，他还没返回结果     //重点看这里，看这里
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我是线程，我是狗蛋爱JAVA，我在运行呢
//我快结束了，准备返回
```  
### 可能有很多语言啊，字啊，打错，但是太累了不想检查太多，你觉得有问题可以留言，我会给你修改的，觉得有用就留一个赞吧


