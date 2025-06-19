# 定时器  ,其实可能早已经淘汰了，改用线程锁代替了  
ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(3);  
使用延迟线程池代替
主要类
java.util.Timer：定时器，调度任务。  
java.util.TimerTask：任务类，相当于 Runnable，但支持取消。  

| 方法                                                  | 描述                                 |
| --------------------------------------------------- | ---------------------------------- |
| `schedule(TimerTask task, long delay)`              | 延迟执行一次任务                           |
| `schedule(TimerTask task, long delay, long period)` | 延迟后**周期性**执行任务（固定频率）               |
| `scheduleAtFixedRate(...)`                          | 和上面类似，但区别是以“固定时间点”为基准重新调度（下文会说明区别） |
| `cancel()`                                          | 取消定时器，停止所有任务                       |


⚠️ Timer 的缺点（很重要）
只有一个线程：  
- 如果任务执行时间长，其他任务会被阻塞。  
- 任务抛出异常会终止整个 Timer：  
- 一个任务挂了，整个定时器都崩了  。
- 不适合复杂调度和并发任务

  | 方法                    | 含义                     | 举例             |
  | --------------------- | ---------------------- | -------------- |
  | `schedule`            | 任务 **执行完再开始计间隔**（时间不准） | 适合不精确的周期任务     |
  | `scheduleAtFixedRate` | **按固定时间间隔调度**（时间更准）    | 适合像钟表一样准点触发的任务 |
