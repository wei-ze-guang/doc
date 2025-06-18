package multithread;


/**
 * 【】Synchronized 可以放哪里
 * 这个关键字如果使用在方法上，别的类调用这个方法的时候就是在获取这个实例监视器，获得之后就会执行方法体里面程序
 * 这个关键字要弄清除，其实完全可以写一个空类，这个类没有任何的同步关键字Synchronized，这个类如果被new对象，他类似于
 * 当作多线程的一个标志来使用，但是一般不推荐这么做，重点是理解这个关键字，要是别的线程调用这个对象监视器，之后他再执行
 * 别的代码程序，就显得别扭
 */
public class SynchronizedClass {

    /**
     * 【】 修饰实例方法
     */
    public synchronized void methodHasSynchronized() {
        // 同步方法，锁的是当前对象(this)
        System.out.println("methodHasSynchronized");
    }

    /**
     * 修饰静态方法
     */
    public static synchronized void staticMethod() {
        // 锁的是当前类的Class对象
    }


    /**
     * 【】下面是修改代码块
     */
    public void method1() {
        synchronized (this) {
            // 只有一个线程可以进入这段代码
            System.out.println("同步代码块，锁的是当前实例对象");
        }
    }




}
