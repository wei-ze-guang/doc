# FutureFuture接口及其衍生接口和实现类
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

## FutureFuture接口
- 源代码
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