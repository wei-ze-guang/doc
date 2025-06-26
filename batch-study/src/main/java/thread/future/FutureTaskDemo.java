package thread.future;


import java.util.concurrent.*;

/**
 * 方式一：Runnable + 结果
 * FutureTask<V> task = new FutureTask<>(Runnable runnable, V result);
 *
 * /方式二：Callable
 * FutureTask<V> task = new FutureTask<>(Callable<V> callable);
 *
 * 不要继承Thread类，因为FutureTask已经实现了Runnable接口，所以可以直接使用FutureTask作为线程的任务。
 */
public class FutureTaskDemo {
    public static void main(String[] args) throws InterruptedException {
//        RunnableDemo();
//        CallableDemo();
//        testCancelMethod();

        testFutureTask();

    }

    /**
     * Runnable这个接口的线程是没有返回值的，为什么下面构造方法传入的""任务执行完成“ 他只是一个标志，如果
     * 返回 ”执行任务完成“，只是说明顺利执行完完成了
     */
    static public void RunnableDemo() {
        Runnable runnable = () -> System.out.println("Runnable task running...");
        FutureTask<String> task = new FutureTask<>(runnable, "任务执行完毕");
        new Thread(task).start();
        String result = null;
        try {
            result = task.get();  // 阻塞，直到任务执行完
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结果：" + result);

    }
    static public void CallableDemo() {
        String res = "这是动态返回结果";   //注意看这里
        Callable<String> callable = () -> {return res;};   //注意看这里
        FutureTask<String> task = new FutureTask<>(callable );
        new Thread(task).start();
        String result = null;
        try {
            result = task.get();  // 阻塞，直到任务执行完
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结果：" + result);  //注意看这里

    }

    /**
     * 才是cancel方法
     */
    static public void testCancelMethod() throws InterruptedException {
        FutureTask<String> task = new FutureTask<>(new runnableImpl(),"执行完成");
        new Thread(task).start();
        boolean cancel1 = true;
        System.out.println("准备取消这个线程，并且传入参数是:"+cancel1);
        Thread.sleep(1000);
        task.cancel(true);
    }

    static void testFutureTask(){
        /**
         * 这里有泛型，大家不懂的话还得去学
         * 这个FutureTask就是包装一个线程，他不能主动运行的
         */
        FutureTask<String> task = new FutureTask<>(new testFutureTaskThread());

        new Thread(task).start();  //只有这个才能运行他

        String result = null;

        try {
            boolean cancelled = task.isCancelled();
            System.out.println("看一下有没有被取消过"+cancelled);
            boolean done = task.isDone();
            System.out.println("查看一下看他完成没有,结果是:"+done);
            result= task.get(500, TimeUnit.MICROSECONDS); //这里肯定不到1秒
        }
            catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            System.out.println("超时了，他还没返回结果");
        } finally {
            if (result != null) {
                System.out.println("有结果了,结果是:"+result);

                boolean cancel = task.cancel(true);//试一下取消

                System.out.println("取消的结果是:"+cancel);  //这里线程都结束了，肯定取消不了 false
            }
        }


    }

    static class testFutureTaskThread implements Callable<String> {

        @Override
        public String call() throws Exception {
                try{
                    Thread.sleep(1000);  //这里至少一秒才会完成，
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    for (int i = 0; i <10; i++){
                        System.out.println("我是线程，我是狗蛋爱JAVA，我在运行呢");
                    }
                }
                System.out.println("我快结束了，准备返回结果");

            return "我是返回的结果";
        }
    }

    static class runnableImpl implements Runnable {
        @Override
        public void run() {

            /**
             * 这里是需要使用Thread.currentThread() 的，因为只写 isInterrupted的话是不行的，
             * 他判断的就是现在写的这个线程,就是调用isInterrupted（）方法的线程，这里就是自己
             */
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Runnable task running...");
                try {
                    /**
                     * 就算是sleep，也是会被打断的 wait也会
                     */
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("有人通知我想要取消我了，我要退出了");

        }
    }
}
