package pool;

public interface MyRejectedExecutionHandler {
    void rejectedExecution(Runnable r, MyThreadPoolExecutor executor);
}

