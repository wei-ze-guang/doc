package casimpl;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Cas {

    private final static ReentrantLock lock = new ReentrantLock();

    private static AtomicInteger counter = new AtomicInteger();

    private static int times = 100000; // 每个线程执行的循环次数

    private static   int count = 0;

    private static int threadNums = 30;  //数量


    public static void main(String[] args) {
        for (int i = 0; i < threadNums; i++) {
            new One().start();
        }

        try {
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            counter.compareAndSet(0,count);  //这里是只有一次，不是自旋
            System.out.println("使用了CAS相关的准群率:"+toPercent(counter.get(),times*threadNums));
            System.out.println("count:"+count);
//            System.out.println("没使用CAS的普通变量:"+toPercent(count,times*threadNums));
            System.out.println("使用synchronized:"+toPercent(count,times*threadNums));
            System.out.println("使用ReentrantLock :"+toPercent(count,times*threadNums));
        }
    }

    static class One extends Thread{
        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
//                counter.incrementAndGet();
//                count++;
//                countAdd();
                lock.lock();
                try {
                    count++;
                }finally {
                    lock.unlock();
                }

            }

        }
    }

    static synchronized void countAdd(){
        count++;
    }

    /**
     * 将两个整数相除，返回百分比字符串，保留两位小数（例如："75.00%"）
     * @param numerator 分子
     * @param denominator 分母
     * @return 格式化的百分比字符串
     */
    public static String toPercent(int numerator, int denominator) {
        if (denominator == 0) {
            return "0.00%"; // 或者你也可以返回 "0.00%"，看具体业务需求
        }
        double ratio = (double) numerator / denominator;
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(ratio);
    }
}
