package juc.pc;

import java.util.Random;
import java.util.concurrent.*;

public class MyBlockingQueueTest {
    public static void main(String[] args) {
        Random rand = new Random();
        MyBlockingQueue myBlockingQueue = new MyBlockingQueue(50);

        ExecutorService executorService = getExecutorService();

        for (int i = 0; i < 300; i++) {
            final int finalI = i;
            Runnable runnable = () ->{
                myBlockingQueue.produce(finalI);
            };

            Runnable runnable1 = ()->{
                int consume = myBlockingQueue.consume();
                System.out.println(consume);
            };

            int i1 = rand.nextInt(299);
            if(i1 < 150){
                executorService.submit(runnable);
            }else {
                executorService.submit(runnable1);
            }
        }
    }

    public static ExecutorService getExecutorService() {
        ExecutorService executorService = new ThreadPoolExecutor(
                5,
                30,
                100,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(50),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return executorService;
    }
}
