## Redisson 提供了哪些核心功能？  

| 类型        | 说明                                     |
| --------- | -------------------------------------- |
| 分布式锁      | `RLock`、公平锁、可重入锁、读写锁、信号量、多锁等           |
| 延迟队列      | `RDelayedQueue` + `RQueue`，支持定时消费任务    |
| 限流器       | `RRateLimiter`，支持令牌桶算法                 |
| 布隆过滤器     | `RBloomFilter`，快速判断某个值是否可能存在           |
| 分布式集合/Map | `RMap`、`RSet`、`RList`，线程安全的 Redis 版本集合 |
| 分布式执行器    | `RExecutorService`，在多个 JVM 节点上执行任务     |
| 分布式对象     | AtomicLong、CountDownLatch、Semaphore 等等 |
| 分布式缓存     | 支持注解缓存、过期策略、自动刷新、异步加载                  |
