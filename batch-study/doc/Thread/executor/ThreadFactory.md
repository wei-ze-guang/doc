## 线程工厂（ThreadFactory）作用
- 统一创建线程的入口
- 线程池或其他线程管理工具不会直接用 new Thread()，而是通过线程工厂来创建线程。这样方便统一管理和扩展。
- 定制线程属性
你可以统一给线程设置： 线程名（方便排查和日志） 是否守护线程（Daemon） 优先级 未捕获异常处理器 线程组 其他自定义逻辑（比如监控创建的线程数）
隔离不同线程池线程的线程命名和管理 线程工厂可以给不同线程池生产有差异的线程，比如命名不同、行为不同。  
## Executors下面的一个线程工厂案例  
- 自己创建线程工厂的话就安全下面实现ThreadFactory接口实现就可以了
```java
    /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                          poolNumber.getAndIncrement() +
                         "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
```