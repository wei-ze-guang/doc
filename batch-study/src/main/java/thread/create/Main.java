package thread.create;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * 通过继承Thread类来创建一个线程
 */
public class Main {
    public static void main(String[] args) {

        Set<Thread> threadSet = new HashSet<Thread>();

        Thread thread = new Thread();
        threadSet.add(thread);

        Thread thread1 = new Thread(thread);
        threadSet.add(thread1);

        Thread thread2 = new Thread(thread1);
        threadSet.add(thread2);

        for (Thread t : threadSet) {
            t.start();
        }

        Thread thread3 = Thread.currentThread();//

        MyThead myThead = new MyThead();  //  注意看这里

        myThead.start();

    }



    @Test
    public void test1() {
        // 这个是全局唯一的，只要拿到他就能拿到它内部资源，
        Thread thread = new MyThead();

        System.out.println("创建了一个线程，线程ID是：  "+thread.hashCode());

        thread.start();
    }

    static class MyThead extends Thread {
        @Override
        public void run() {
            // 我们想在线程内部获取我们自己，这样子获取
            Thread currentThread = Thread.currentThread();//  注意看这里
            System.out.println("线程ID是：  "+currentThread.hashCode());
            anyWhereMethod();
        }
    }

    public static void anyWhereMethod() {
        //这是一个普通方法，任何线程都可以访问，任何线程执行到了都能获取他自己
        Thread thread = Thread.currentThread();
        System.out.println("线程ID是：  "+thread.hashCode());

        String name = thread.getName();

        System.out.println(name);
    }

    @Test
    public void testThreadLocal() {


    }




    @Test
    public void test() {

        Set<Thread> threadSet = new HashSet<Thread>();

        Thread thread = new Thread();
        threadSet.add(thread);

        Thread thread1 = new Thread(thread);
        threadSet.add(thread1);

        Thread thread2 = new Thread(thread1);
        threadSet.add(thread2);

        for (Thread t : threadSet) {
            t.start();
        }

        Thread thread3 = Thread.currentThread();

    }

    static void  t(){
        ThreadByExtendsThread thread = new ThreadByExtendsThread();

        ThreadByImplRunnable runnable = new ThreadByImplRunnable();

        ThreadContextInterface threadContextInterface = new ThreadContextInterfaceImpl(runnable);

        Thread thread1 = threadContextInterface.getThreadContext();

        thread1.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
