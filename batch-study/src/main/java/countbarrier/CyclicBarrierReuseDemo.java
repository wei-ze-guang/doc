package countbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierReuseDemo {

    static final int parties = 3;
    static  CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
        System.out.println(">>> 所有人到达屏障点，开始下一阶段\n");
    });

    public static void main(String[] args) {

        for (int i = 0; i < parties; i++) {
            new Thread(new Worker(barrier, i)).start();
        }

    }
    static class Worker implements Runnable {

        private final CyclicBarrier cyclicBarrier;

        private final int id;

        public Worker(CyclicBarrier cyclicBarrier, int id) {
            this.cyclicBarrier = cyclicBarrier;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                for (int round = 1; round <= 3; round++) {
                    System.out.println("线程 " + id + " 第 " + round + " 轮准备...");
                    Thread.sleep((long)(Math.random() * 1000)); // 模拟任务
                    System.out.println("线程 " + id + " 第 " + round + " 轮就绪，等待其他人...");
                    barrier.await(); // 每一轮都同步一次 第一次循环调用的时候，就已经卡在这里了，
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
