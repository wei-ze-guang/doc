package thread.create;

/**
 * 通过继承Thread类来创建一个线程
 */
public class CreateByExtendsThread {
    public static void main(String[] args) {
        Thread thread = getThread();
        thread.start();
    }

    static Thread getThread() {
        return new Thread() ;
    }
}
