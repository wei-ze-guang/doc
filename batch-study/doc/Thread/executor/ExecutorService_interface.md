## ExecutorService 接口  

###     void shutdown(); 方法  
- 这个方法是幂等的，就是整个线程池你只能运行一次这个方法，你再次调用后面也没用了。幂等性
- 
| 任务状态                  | 会继续执行吗？ | 说明                                                        |
| --------------------- | ------- |-----------------------------------------------------------|
| 正在运行中的任务              | ✅ 会执行完  | 线程池不会中断正在运行的线程                                            |
| 排队等待的任务（队列中）          | ✅ 会执行   | 在线程空闲后，从队列中取出执行                                           |
| `shutdown()` 之后新提交的任务 | ❌ 不会执行  | 会被拒绝执行，抛出 `RejectedExecutionException`,这个怎么处理和用户设置的拒绝策略有关 |
```java
/**
 * 启动一个有序的关闭过程：已提交的任务会被执行完，但不会再接受新的任务。
 *
 * 注意：此方法不会等待已提交任务的执行完成。
 * 如果你需要等待它们完成，请使用 {@link #awaitTermination awaitTermination} 方法。
 *
 * @throws SecurityException 如果存在安全管理器，并且关闭此 ExecutorService
 *         可能会操作调用者无权限修改的线程，比如调用者没有
 *         {@link java.lang.RuntimePermission}{@code ("modifyThread")} 权限，
 *         或者安全管理器的 {@code checkAccess} 方法拒绝访问。
 */
void shutdown();

```  
###     List<Runnable> shutdownNow(); 方法  虽然官方没有说这个方法是否是幂等性，但是他是一个幂等性的方法
1. shutdownNow() 调用后，线程池会处于什么状态？
- 调用 shutdownNow() 后，线程池进入 停止状态（STOP）。在这个状态下：
  - 不会再接受新的任务。
  - 已提交但未开始执行的任务会被返回。
  - 正在执行的任务会被尝试中断。
2. 如果此时又有新的任务提交，会发生什么？
- 线程池拒绝接受新的任务。
- 新提交的任务会被拒绝，通常会抛出 RejectedExecutionException。
3. 这个“拒绝”由谁来控制？
- ThreadPoolExecutor 有一个 拒绝策略（RejectedExecutionHandler）：
默认是 AbortPolicy，它会直接抛异常 RejectedExecutionException。
也可以自定义拒绝策略（如丢弃任务、将任务交给调用线程执行等）。
```java
/**
 * 尝试立即停止所有正在执行的任务，停止处理正在排队等待的任务，
 * 并返回那些尚未开始执行的任务列表。
 *
 * 注意：此方法不会等待正在执行的任务终止。
 * 如果你需要等待它们终止，请使用 {@link #awaitTermination awaitTermination} 方法。
 *
 * 此方法只能“尽力而为”地尝试停止正在执行的任务，并不能保证一定能成功。
 * 例如，典型实现会通过 {@link Thread#interrupt} 取消任务，
 * 所以如果某些任务无法响应中断，它们可能永远不会终止。
 *
 * @return 尚未开始执行的任务列表
 * @throws SecurityException 如果存在安全管理器，并且关闭此 ExecutorService
 *         可能会操作调用者无权限修改的线程，比如调用者没有
 *         {@link java.lang.RuntimePermission}{@code ("modifyThread")} 权限，
 *         或者安全管理器的 {@code checkAccess} 方法拒绝访问。
 */
List<Runnable> shutdownNow();

```  
###     boolean isShutdown();方法，返回的结果就是你是否进行过isShutDown()或者isShutDownNow() ,如果是就返回true不是的话返回false
```java
/**
 * 如果此执行器已经被关闭，则返回 {@code true}。
 *
 * @return 如果此执行器已关闭，则返回 {@code true}，否则返回 {@code false}。
 */
boolean isShutdown();

```  
### boolean isTerminated();方法
- 这个方法的前提是尝试过对线程池进行关闭，他会等到所有任务的线程执行完毕，并不是说只把线程调度完毕，需要所有的线程都已经返回结果   
- 
  | 你的理解             | 实际情况                  |
  | ---------------- | --------------------- |
  | 只要把任务“交给线程”就算完成？ | 不是，任务必须真正执行完毕才算完成     |
  | 任务没结果线程池会忽略吗？    | 不会，线程池会等任务执行结束再算完成    |
  | 任务卡死怎么办？         | 线程池没法“知道”，需要外部中断或超时机制 |

```java
/**
 * 如果所有任务在关闭后都已完成，则返回 {@code true}。
 * 注意，除非先调用了 {@code shutdown} 或 {@code shutdownNow}，
 * 否则 {@code isTerminated} 永远不会返回 {@code true}。
 *
 * @return 如果所有任务在关闭后都已完成，则返回 {@code true}，否则返回 {@code false}。
 */
boolean isTerminated();

```  
### 演示 isShutdown() 和 isTerminated() 的区别和用法：
```java
import java.util.concurrent.*;

public class ShutdownVsTerminatedDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 提交一个耗时任务
        executor.submit(() -> {
            try {
                System.out.println("任务开始执行...");
                Thread.sleep(3000);
                System.out.println("任务执行完毕");
            } catch (InterruptedException e) {
                System.out.println("任务被中断");
            }
        });

        // 调用 shutdown，开始关闭，不接受新任务，但会执行完已提交任务
        executor.shutdown();

        System.out.println("调用 shutdown 后：");
        System.out.println("isShutdown() = " + executor.isShutdown());    // true
        System.out.println("isTerminated() = " + executor.isTerminated()); // false，因为任务还没执行完

        // 等待任务执行完毕，最长等5秒
        if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("所有任务执行完毕");
        } else {
            System.out.println("等待超时，仍有任务未完成");
        }

        System.out.println("等待结束后：");
        System.out.println("isShutdown() = " + executor.isShutdown());    // 仍然是 true
        System.out.println("isTerminated() = " + executor.isTerminated()); // 任务执行完后变 true
    }
}

```  
###     boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException; 方法，这个方法会堵塞  
这个方法可以让你 阻塞当前线程一段时间，等待线程池彻底关闭（即 isTerminated() == true），非常适合配合 shutdown() 一起使用：  
```java
executor.shutdown(); // 发起关闭请求
executor.awaitTermination(10, TimeUnit.SECONDS); // 等待最多10秒，直到所有任务执行完毕

```  
```java
/**
 * 阻塞当前线程，直到以下三种情况之一发生（以最先发生的为准）：
 * 1. 所有任务在调用 shutdown 请求后执行完毕；
 * 2. 等待时间超时；
 * 3. 当前线程在等待过程中被中断。
 *
 * @param timeout 最长等待时间
 * @param unit    timeout 的时间单位
 * @return 如果在线程池终止之前等待时间未超时，则返回 {@code true}；
 *         如果在终止前等待超时，则返回 {@code false}
 * @throws InterruptedException 如果在等待期间当前线程被中断，则抛出此异常
 */
boolean awaitTermination(long timeout, TimeUnit unit)
    throws InterruptedException;

```  
###     <T> Future<T> submit(Callable<T> task);方法  
- 作用： 提交一个带返回值的任务；
  - 返回值： Future<T>，你可以通过它： 检查任务是否完成； 获取执行结果（调用 .get()，会阻塞直到任务完成）； 或取消任务（.cancel(true)）；
- 常见写法：  
```java
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

System.out.println("计算结果: " + future.get()); // 会阻塞直到结果返回

```  
```java
/**
 * 提交一个可返回结果的任务用于执行，并返回一个表示该任务的 Future。
 * 当任务成功完成时，Future 的 {@code get} 方法将返回任务的结果。
 *
 * <p>
 * 如果你希望立即阻塞等待任务完成，可以使用如下形式：
 * {@code result = executor.submit(aCallable).get();}
 *
 * <p>注意：{@link Executors} 类包含一些工具方法，
 * 可以将其他常见的类似闭包的对象（如 {@link java.security.PrivilegedAction}）
 * 转换为 {@link Callable} 形式，从而允许提交执行。
 *
 * @param task 要提交的任务
 * @param <T>  任务结果的类型
 * @return 表示该任务尚未完成的 Future
 * @throws RejectedExecutionException 如果任务无法被调度执行
 * @throws NullPointerException 如果任务为 null
 */
<T> Future<T> submit(Callable<T> task);

```  
###    <T> Future<T> submit(Runnable task, T result); 方法
- 通俗理解：
你提交了一个 没有返回值的任务（Runnable）； 但你又希望任务完成后，通过 Future.get() 获得一个“固定结果”，那就传个 result； 所以这个方法就像是在执行完 Runnable 后，帮你“附带返回一个指定结果”。
- 常用写法
```java
ExecutorService executor = Executors.newSingleThreadExecutor();

Runnable task = () -> System.out.println("执行一个任务...");
Future<String> future = executor.submit(task, "任务已完成");

String result = future.get(); // 阻塞等待任务执行完，然后返回 result
System.out.println(result);   // 输出：任务已完成

```  
```java
/**
 * 提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。
 * 当任务成功完成后，Future 的 {@code get} 方法将返回指定的结果。
 *
 * @param task   要执行的任务
 * @param result 任务成功完成后，通过 Future 的 {@code get()} 返回的结果
 * @param <T>    返回结果的类型
 * @return 表示任务尚未完成的 Future
 * @throws RejectedExecutionException 如果任务无法被调度执行
 * @throws NullPointerException 如果任务为 null
 */
<T> Future<T> submit(Runnable task, T result);

```  
###     Future<?> submit(Runnable task); 方法  
- 通俗解释：
这是最常见的 submit() 形式，用来提交一个没有返回值的任务；  
你仍然会获得一个 Future<?>，可以： 检查任务是否完成； 用 .get() 等待执行结果，但返回值是 null； 可以取消任务（通过 .cancel(true)）;  
- 示例代码  
```java
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<?> future = executor.submit(() -> {
    System.out.println("正在执行一个无返回值任务...");
});

future.get(); // 返回 null，表示任务执行成功完成
System.out.println("任务执行完毕");

```  
```java
/**
 * 提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。
 * 当任务 <em>成功</em> 完成时，Future 的 {@code get()} 方法将返回 {@code null}。
 *
 * @param task 要提交的任务
 * @return 表示该任务尚未完成的 Future
 * @throws RejectedExecutionException 如果任务无法被调度执行
 * @throws NullPointerException 如果任务为 null
 */
Future<?> submit(Runnable task);

```  
### 三种submit方法对比  
| 方法签名                              | 任务类型   | `Future.get()` 返回值 |
| --------------------------------- | ------ | ------------------ |
| `submit(Callable<T> task)`        | 有返回值任务 | 返回任务执行结果（T）        |
| `submit(Runnable task, T result)` | 无返回值任务 | 返回你传入的 `result` 值  |
| `submit(Runnable task)`           | 无返回值任务 | 返回 `null`          |
###     <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException; 方法    
**注意看这里**  
```java
public void run() {
    try {
        V result = callable.call();  // 执行任务的核心代码
        // 任务执行完毕，保存结果等后续操作
    } catch (Exception e) {
        // 任务执行异常处理
    }
}

```
- 通俗理解：就是保证全部线程实行了run方法，结果你自己去取
  - 你可以一次性提交一堆 Callable 任务，让线程池并发执行它们，并且：
    - 等所有任务都执行完你才会继续；
    - 每个任务你都能拿到一个 Future；
    - 阻塞当前线程，直到全部执行完。    
- 特别注意 要求所有的从提交到最后全部执行完
提交所有任务 → 阻塞当前线程 → 等待这些任务全部执行完成 / 或当前线程被中断 → 返回结果 
- 再注意  
  invokeAll() 等的是：所有任务的 call() 方法已经真正执行完（线程运行完）， 而不是仅仅调度完或放入线程池。**注意是线程的具体任何的call（）就是我们写的代码，执行完才算
- 特点  

| 特性         | 描述                                           |
| ---------- | -------------------------------------------- |
| 是否阻塞       | 是，直到所有任务完成或被中断                               |
| 返回什么       | `List<Future<T>>`，与传入任务顺序一致                  |
| 任务类型       | 必须是 `Callable`，不能是 `Runnable`                |
| 如果某个任务抛异常  | 调用对应的 `future.get()` 时抛 `ExecutionException` |
| 中途被中断会怎么样？ | 抛 `InterruptedException`，但已开始执行的任务可能仍会继续运行   |   
- 线程被中断的意思是  ,示例代码
```java
Thread mainThread = Thread.currentThread();
mainThread.interrupt(); // 别的线程这么干

executor.invokeAll(tasks); // 💥 抛 InterruptedException（主线程在等待时被打断）

```
- 示例代码  
```java
ExecutorService executor = Executors.newFixedThreadPool(3);

List<Callable<Integer>> tasks = List.of(
    () -> 1 + 1,
    () -> 2 + 2,
    () -> 3 + 3
);

List<Future<Integer>> results = executor.invokeAll(tasks);

for (Future<Integer> future : results) {
    System.out.println("结果: " + future.get()); // get 会阻塞，但此处任务已经完成
}

executor.shutdown();

```  
```java
/**
 * 执行给定的所有 Callable 任务，当所有任务执行完毕后（或调用过程中发生异常中断），
 * 返回一个包含每个任务对应 Future 的列表。
 *
 * 该方法会阻塞，直到：
 * - 所有任务都执行完成，或
 * - 当前线程在等待期间被中断（此时会抛出 InterruptedException）。
 *
 * 返回的每个 Future 都表示一个已完成的任务：
 * - 如果任务正常完成，Future 的 {@code get()} 方法返回任务结果；
 * - 如果任务抛出异常，调用 {@code get()} 会抛出 ExecutionException；
 * - 如果任务被取消，调用 {@code get()} 会抛出 CancellationException。
 *
 * @param tasks 要执行的任务集合
 * @param <T>   每个任务返回结果的类型
 * @return 包含每个任务对应 Future 的列表，顺序与传入的任务顺序一致
 * @throws InterruptedException 如果当前线程在等待过程中被中断
 */
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
    throws InterruptedException;

```   
###  1. invokeAll() 和 isTerminated() 的本质区别
| 方面            | `invokeAll()`           | `isTerminated()`             |
| ------------- | ----------------------- | ---------------------------- |
| **作用对象**      | 线程池执行的一批任务              | 线程池本身的状态                     |
| **功能**        | 阻塞调用线程直到给定的所有任务都执行完毕    | 判断线程池是否已经完全关闭（所有任务结束且线程资源释放） |
| **是否阻塞**      | 是，调用时会阻塞直到所有任务完成或当前线程中断 | 否，立刻返回当前线程池是否终止的状态           |
| **使用时机**      | 你想提交一组任务并等待它们全部完成时使用    | 你想检查线程池是否已经完全关闭时使用           |
| **是否反映线程池状态** | 不直接反映，只反映这批任务的执行情况      | 是，表示线程池已关闭并且所有任务都执行完成        |  

###     <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException; 方法 
- 这个方法也是 一个任务完成没完成标志是线程的run方法是否已经执行  
- 该方法会在以下三种情况之一时返回：
  - 所有任务执行完毕（成功或异常）；
  - 超过指定超时时间；
  - 当前线程被中断。
  - 如果超时返回，部分任务可能没执行完，调用对应 Future 的 isDone() 可判断任务是否完成。  
```java
/**
 * 执行给定的一批 Callable 任务，阻塞等待所有任务完成，或达到超时时间，或当前线程被中断，
 * 以先发生者为准。
 *
 * 返回一个包含每个任务对应 Future 的列表。列表顺序与传入任务集合顺序一致。
 *
 * 如果在指定超时时间内所有任务都完成，返回的 Future 都是已完成状态；
 * 如果超时，则部分任务可能仍未完成，对应的 Future 调用 {@code isDone()} 返回 false。
 *
 * 调用者可通过 Future 的 {@code get()} 方法获取任务结果，
 * 如果任务抛出异常，则 {@code get()} 抛出 ExecutionException。
 *
 * @param tasks   要执行的任务集合
 * @param timeout 最长等待时间
 * @param unit    时间单位
 * @param <T>     任务返回结果类型
 * @return 表示所有任务的 Future 列表
 * @throws InterruptedException 如果当前线程在等待期间被中断
 */
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                             long timeout, TimeUnit unit)
    throws InterruptedException;

```  
###     <T> T invokeAll(Collection<? extends Callable<T>> tasks,long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;方法  
- 额外说明：
  - invokeAny 只要有一个任务成功完成就返回它的结果，其他任务会被取消；
  - 如果超时或线程中断，则会提前结束等待并抛异常；
  - 适用于你只关心最快返回的那个任务结果的场景。  
```java
/**
 * 执行给定的一批 Callable 任务，返回其中一个成功完成任务的结果。
 *
 * 方法会阻塞，直到：
 * - 某个任务成功完成并返回结果，
 * - 或达到指定超时时间，
 * - 或当前线程被中断。
 *
 * 如果在超时前没有任何任务成功完成，抛出 TimeoutException；
 * 如果当前线程在等待期间被中断，抛出 InterruptedException；
 * 如果所有任务执行完成但没有成功的，抛出 ExecutionException（通常封装最后一个异常）。
 *
 * @param tasks   要执行的任务集合
 * @param timeout 最长等待时间
 * @param unit    时间单位
 * @param <T>     任务返回结果类型
 * @return 某个成功完成任务的结果
 * @throws InterruptedException 如果当前线程在等待期间被中断
 * @throws ExecutionException   如果所有任务都未成功完成
 * @throws TimeoutException     如果超时未有任务成功完成
 */
<T> T invokeAny(Collection<? extends Callable<T>> tasks,
                long timeout, TimeUnit unit)
    throws InterruptedException, ExecutionException, TimeoutException;

```  
###     <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException;方法  
- 说明：
  - 和带超时版本相比，区别是没有超时限制，会一直阻塞直到：
    - 有任务成功完成返回结果；
    - 或所有任务失败抛 ExecutionException；
    - 或当前线程被中断抛 InterruptedException。
    - 一旦有任务成功返回，其他任务会被取消，不会浪费资源。  
```java
/**
 * 执行给定的一批 Callable 任务，返回其中一个成功完成的任务结果（即任务未抛异常）。
 * 一旦某个任务正常返回或抛出异常，所有未完成的任务都会被取消。
 * 注意：如果在执行过程中修改传入的任务集合，结果是未定义的。
 *
 * @param tasks 要执行的任务集合
 * @param <T> 任务返回值类型
 * @return 某个成功完成任务的结果
 * @throws InterruptedException 如果当前线程在等待时被中断
 * @throws NullPointerException 如果任务集合或其中任何任务为 null
 * @throws IllegalArgumentException 如果任务集合为空
 * @throws ExecutionException 如果所有任务都未成功完成（全部失败）
 * @throws RejectedExecutionException 如果任务无法被调度执行
 */
<T> T invokeAny(Collection<? extends Callable<T>> tasks)
    throws InterruptedException, ExecutionException;

```  
### invokeAll 四个方法对比与总结
| 方法签名                                                                                                  | 阻塞行为              | 超时支持 | 返回结果                | 异常处理                                                   | 适用场景                     |
| ----------------------------------------------------------------------------------------------------- | ----------------- | ---- | ------------------- | ------------------------------------------------------ | ------------------------ |
| `<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)`                              | 阻塞直到所有任务完成（成功或异常） | 无    | 所有任务对应的 `Future` 列表 | 当前线程中断抛 `InterruptedException`                         | 等待所有任务完成，适合需要所有结果的批量处理   |
| `<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)` | 阻塞直到所有任务完成或超时或中断  | 有    | 所有任务对应的 `Future` 列表 | 超时抛 `TimeoutException`，中断抛 `InterruptedException`      | 需要批量执行任务，但不能无限等待，超时后继续处理 |
| `<T> T invokeAny(Collection<? extends Callable<T>> tasks)`                                            | 阻塞直到有一个任务成功完成     | 无    | 某一个任务成功结果           | 中断抛 `InterruptedException`，无成功任务抛 `ExecutionException` | 只关心最快返回的一个任务结果，节省等待时间    |
| `<T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)`               | 阻塞直到有一个任务成功完成或超时  | 有    | 某一个任务成功结果           | 超时抛 `TimeoutException`，中断抛 `InterruptedException`      | 关心最快返回结果，但等待时间有限，超时要处理   |

### 详细总结：
| 维度       | `invokeAll(Collection)`          | `invokeAll(Collection, timeout)`                    | `invokeAny(Collection)`                                   | `invokeAny(Collection, timeout)`                                                 |
| -------- | -------------------------------- | --------------------------------------------------- | --------------------------------------------------------- | -------------------------------------------------------------------------------- |
| **阻塞方式** | 阻塞直到所有任务完成（无超时限制）                | 阻塞直到所有任务完成或超时                                       | 阻塞直到有一个任务成功完成                                             | 阻塞直到有一个任务成功完成或超时                                                                 |
| **超时支持** | 不支持超时                            | 支持超时                                                | 不支持超时                                                     | 支持超时                                                                             |
| **返回类型** | List\<Future\<T>>，包含所有任务的 Future | List\<Future\<T>>，包含所有任务的 Future                    | 任务的结果类型 T                                                 | 任务的结果类型 T                                                                        |
| **异常抛出** | 线程中断抛 `InterruptedException`     | 线程中断抛 `InterruptedException`，超时抛 `TimeoutException` | 线程中断抛 `InterruptedException`，所有任务失败抛 `ExecutionException` | 线程中断抛 `InterruptedException`，超时抛 `TimeoutException`，所有任务失败抛 `ExecutionException` |
| **任务取消** | 不自动取消任务                          | 超时返回后，部分未完成任务可能仍在执行                                 | 一旦有任务成功，其他任务会被取消                                          | 一旦有任务成功，其他任务会被取消                                                                 |
| **适用场景** | 需要等待所有任务完成，获取所有任务结果              | 需要等待所有任务完成，但有最大等待时间限制                               | 只关心最快任务结果，其他任务不关心                                         | 只关心最快任务结果，且设置最大等待时间                                                              |
