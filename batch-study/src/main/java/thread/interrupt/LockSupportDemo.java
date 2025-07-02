package thread.interrupt;

import java.io.IOException;

/**
 * ；练习LockSupport
 */
public class LockSupportDemo {

    public static void main(String[] args) throws  IOException {
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
//            LockSupport.park(blocker);//
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getCause());
            }
//            LockSupport.setCurrentBlocker(null);
//            LockSupport.parkNanos(blocker,1000L);
            System.out.println("子线程被唤醒了");
        },"my-thread");

        t.start();

        try {
            Thread.sleep(100L); // 保证子线程先 park
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        System.out.println("主线程开始 unpark");
        t.interrupt();// 直接打断线程的话，线程不会抛出异常会马上返回 ，如果里面是sleep的话会被打断的并且抛出异常
//        LockSupport.unpark(t); // 唤醒子线程
        System.in.read();
    }

}
