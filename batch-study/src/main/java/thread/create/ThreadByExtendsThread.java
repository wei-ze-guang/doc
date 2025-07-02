package thread.create;


/**
 * 这个接口是每一种策略需要实现的功能，策略模式要求的是实现方式不同，但是结果要一样
 * 下面的Thread的原始  public class Thread implements Runnable 他实现了Runnable接口，是一个类
 * 下面是Thread 实现Runnable接口的具体
 * @Override
 *     public void run() {
 *         if (target != null) {
 *             target.run();
 *         }
 *     }
 *     也就是说直接new 一个 Thread类就可以运行，但是target为null  什么都不执行，我们通过继承Thread重写他的方法就可以了
 */

/**
 * 这个实现类现在是实现接口，是可选策略之一
 */
public class ThreadByExtendsThread implements CreateThreadInterface{
    @Override
    public Thread createThread() {
        /**
         * 这种方法的话执行体是空的不会出错的
         */
        return new Thread();
    }
    /**
     * 只需要实现上面的接口方法，但是我们可以扩展
     */

    private Thread getRowThread() {
        /**
         * 这种就是啥也不做,原生的
         */
        return getExtendsThread();
    }

    private Thread getExtendsThread() {
        /**
         * 这种就是啥也不做,原生的
         */
        return new ExtendsThread();
    }

    class ExtendsThread extends Thread{
        @Override
        public void run() {
            System.out.println("通过继承Thread类重写他的run方法创建的线程");
        }
    }
}
