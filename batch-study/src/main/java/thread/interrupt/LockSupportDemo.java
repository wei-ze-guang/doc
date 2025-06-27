package thread.interrupt;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

/**
 * ；练习LockSupport
 */
public class LockSupportDemo {

    public static void main(String[] args) throws InterruptedException, IOException {
//        System.out.println("开始");
//
//        LockSupport.park();  //这里也需要防止虚假唤醒，使用while循环一下
//
//        System.out.println("如果打印出来就是没有被挂起");

        Thread t = new Thread(() -> {
            System.out.println("子线程开始执行");
//            LockSupport.park(); // 挂起自己
//            LockSupport.park(1000);
//            LockSupport.parkNanos(1000L);  //使用parkNanos
            Object blocker = new Object();
//            LockSupport.setCurrentBlocker(blocker);
            LockSupport.park(blocker);//
//            LockSupport.setCurrentBlocker(null);
//            LockSupport.parkNanos(blocker,1000L);
            System.out.println("子线程被唤醒了");
        },"my-thread");

        t.start();

        Thread.sleep(100000000L); // 保证子线程先 park
        System.out.println("主线程开始 unpark");
        LockSupport.unpark(t); // 唤醒子线程
        System.in.read();
    }

    static void t(){

    }
}
