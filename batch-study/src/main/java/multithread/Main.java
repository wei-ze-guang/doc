package multithread;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 这里是练习多线程的
 */

/**
 * 并发的三个特性，原子性，顺序性，可见性
 */

public class Main {
    /**
     * 下面是三种不同方式的启动方式
     *
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    /**
     * 用来测试共享资源是实例成员和类成员的区别
     */
    private int testPublicResource = 0;

    /**
     * 这个用来测试使用外部类的对使用了Synchronized的监视器，是实例
     */
    private SynchronizedClass synchronizedClass = new SynchronizedClass();

    /**
     * flag
     *
     */

    private static  Boolean flag = false;


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        for (int i = 0; i < 10; i++) {
        new OneThread().start();
        new  Thread(new RunnableImpl()).start();

        FutureTask[] array = new FutureTask[10];

        for (int j = 0; j < 10; j++) {
            array[j] = new FutureTask(new CallableImpl());
            array[j].run();
            Integer result = (Integer) array[j].get();
        }

        for (int j = 0; j < 10; j++) {
            System.out.println("线程返回值是:"+array[j].get());
        }

        }
    }

    /**
     * 这里练习多线程下资源不安全情况，不做任何处理
     */
    @Test
    public void testPublicResource() {
        int i = 0;
        float length = 2F;
        for (i = 0; i < length; i++) {
            new OneThread().start();
        }

        try {
            //这个sleep  谁调用谁睡眠，看清楚是谁调用，不是当前线程,锁不释放
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("最后的结果是" + OneThread.testPublicResource + "预期结果是:" + (OneThread.testPublicResource * 100) / (length * OneThread.time) + "%");
        }

    }

    /**
     * 【】这里测试是没有任何初始的共享资源测试
     * 这个和上面那个差不多，只不过是测试的是实例成员变量，上面测试的是静态的类变量，使用了static
     * @throws InterruptedException
     */
    @Test
    public void testPublicResourcePrimary() throws InterruptedException {
        int threadCount = 50;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    testPublicResource++; // 非原子操作，容易产生线程安全问题
                }
            });
            threads[i].start();
        }
        /**
         * 这是了一个守护进程，jvm中其实还有很多守护进程，比如垃圾回收，main是一个用户进程
         * 如果 不设置下面的true，默认开启的是用户进程，只有所有用户进程结束之后jvm才会停机，不管你守护进程有或者没有在运行
         */
        threads[2].setDaemon(true);

        // 等待所有线程执行完毕，如果在使用t.join()之前这个线程已经执行完了，不用管
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("最终结果是：" + testPublicResource + "，预期是：" + (threadCount * 1000));
    }

    /**
     * 【】测试wait\
     * 从别的类获取监视器
     */
    @Test
    public void testWait() throws InterruptedException {
        synchronized (synchronizedClass) {
            synchronizedClass.wait();
        }

    }

    public void TestObjThis(){
        synchronized (this){

        }
    }

    /**
     *
     * @return
     */
    private synchronized int getRandom() {
        Random random = new Random();
        return random.nextInt(100);
    }

    /**
     * 用来测试
     */
    synchronized void testSynchronized() {
        System.out.println("testSynchronized");
    }

    /**
     * 【】await是线程被挂起自动释放锁，wait是实例的方法
     * 写在内部好一点可以可以直接访问资源
     */
    class OneInnerThread extends Thread {
        @Override
        public void run() {
            //因为这个方法在现在的Main实例的内部，现在获取的是现在这个实例的监视器
            testSynchronized();
            /**
             * //  这是是某种情况，我们等待，为什么等待，因为我们还没拿到我想要的资源，这个条件就是我们想要的资源
             * 下面!条件成立我们就等待，为什么不使用if而是使用一个无限循环while呢，可以看下面
             * 不使用if是有因为，一旦使用了if他之后判断一次，那么这个if成立之后他就会等待，但是这个时候被虚假唤醒的话，这个线程就会返回，继续执行，但是
             *   此时条件依然不是我们我想要的记过
             *    if (!条件成立) {
             *         wait();  // 唤醒一次就执行下面逻辑，可能误判
             *     }
             *     // 条件可能没满足，出现逻辑错误
             */
            //这里其实写的有问题，因为上面已经还没执行，一般来说上面的同步方法执行完之后这里就不要了，只是为了写
            // 虚假唤醒（Spurious Wakeup）”，虚假唤醒概率是比较小的是指一个正在执行 wait() 或 await() 的线程，在没有收到 notify() 或 signal() 通知、
            // 也没有条件满足的情况下被虚假唤醒了。这里的话如果在调用wait之前，上面的同步方法已经执行完了，所以下面要加一个条件
            while (true) {   // 用循环判断条件，避免“虚假唤醒”和“错过通知”，这个这个条件true不是我们想要的，我们想要的是false，如果不是这个条件线程被虚假唤醒，在wait一次

                try {
                    wait();  // 可能是虚假唤醒，再检查一次条件
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    class TestVolatileThread extends Thread {
        public static volatile boolean flag = false;
        @Override
        public void run() {
            flag = true;
        }
    }
}

/**
 * 【】这里测试是没有任何初始的共享资源测试
 * 这种方法的好处是可以传参，在里面定义成员变量，这种方法是无返回的
 * 不好的是java只能单继承，继承了这个之后不能再继承别的类
 */
class OneThread extends Thread {
    public static int time = 1000000000;  //次数
    public static int testPublicResource = 0;

    @Override
    public void run() {
        for (int i = 0; i < time; i++) {
            testPublicResource ++;
//            System.out.println("现在的线程是:"+Thread.currentThread().getName()+",i:"+i);
        }
    }
}

/**
 *好处是可以多继承，一样这种方法也是没有返回值的
 * 这个和上面那个启动方式不一样的
 */
class RunnableImpl implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("现在的线程是:"+Thread.currentThread().getName()+",i:"+i);
        }
    }
}

/**
 * 有点是有返回值
 */
class CallableImpl implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("现在的线程是:" + Thread.currentThread().getName() + ",i:" + i);
        }
        //这里
        return new Random().nextInt(10);
    }
}


