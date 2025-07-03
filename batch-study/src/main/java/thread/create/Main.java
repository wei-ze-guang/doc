package thread.create;

/**
 * 通过继承Thread类来创建一个线程
 */
public class Main {
    public static void main(String[] args) {
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
