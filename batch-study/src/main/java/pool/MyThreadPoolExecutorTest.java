package pool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadPoolExecutorTest {
    public static void main(String[] args) {

        final  AtomicInteger atomicInteger = new AtomicInteger();

        MyThreadPoolExecutor myThreadPoolExecutor = new MyThreadPoolExecutor(2, 4, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2));

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            myThreadPoolExecutor.execute(() -> {
                System.out.println("任务" + finalI);
                while (atomicInteger.get() < 100000000) {
                    atomicInteger.incrementAndGet();
                }
//
//                try {
//                    Thread.sleep(1);
//                }catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            });

        }

        try {
            Thread.sleep(50000);
            myThreadPoolExecutor.shutdown();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
