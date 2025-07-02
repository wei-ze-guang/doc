package thread.create;

/**
 * 这里是通过实现Runnable接口 ，这是实现一种策略
 */
public class ThreadByImplRunnable implements CreateThreadInterface{
    @Override
    public Thread createThread() {
        return getThread();
    }

    /**
     * 下面的三种方法
     * @return
     */
    private Thread getThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("通过实现Runnable接口创建的线程，通过直接显现Runnable接口，这个线程需要Thread来启动");
            }
        };

        return new Thread(runnable);
    }


//    @Override,这里为了不写的，这个完全可以跟 上面的一摸一样，这是通过匿名内部类实现的
    public Thread createThread1() {
        return new Thread( new Runnable() {
            @Override
            public void run() {
                    System.out.println("通过实现Runnable接口创建的线程，通过匿名内部类，这个线程需要Thread来启动");
            }
        });
    }
    /**
     * lambda 写法（语法糖）：
     * 也可以
     */
    public Thread createThread2() {
           return new Thread( () ->{
                System.out.println("通过实现Runnable接口创建的线程，通过lambda 写法（语法糖）：，这个线程需要Thread来启动");
            });
    }
}
