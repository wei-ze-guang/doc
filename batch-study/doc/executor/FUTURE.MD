## Future接口  
| 方法签名                                            | 说明                                  |
| ----------------------------------------------- | ----------------------------------- |
| `boolean cancel(boolean mayInterruptIfRunning)` | 取消任务，尝试停止任务执行；如果任务已经完成或取消，则返回 false |
| `boolean isCancelled()`                         | 判断任务是否被取消                           |
| `boolean isDone()`                              | 判断任务是否已完成（正常完成、异常结束或被取消都算完成）        |
| `V get()`                                       | 阻塞等待并获取任务结果                         |
| `V get(long timeout, TimeUnit unit)`            | 限时等待并获取任务结果，超时抛出 `TimeoutException` |
## 实现类  
| 实现类名                   | 说明                                                            |
| ---------------------- | ------------------------------------------------------------- |
| `FutureTask<V>`        | 最常用的实现类，既是 `Runnable` 也是 `Future`，封装任务和结果，支持手动启动或线程池执行        |
| 线程池返回的匿名类              | 线程池提交任务（`submit()`）返回的 `Future` 实现，内部可能是 `FutureTask` 或自定义匿名类 |
| `CompletableFuture<V>` | Java 8 新增的 `Future` 子类，支持更丰富的异步组合和回调功能                        |

## 额外说明
- FutureTask 是 Future 的经典实现，可以包装 Callable 或 Runnable 任务。
  - 线程池的 submit() 方法 返回的 Future 实际就是 FutureTask 或其子类的实例。
  - CompletableFuture 是 Future 的增强版，支持链式调用和回调。