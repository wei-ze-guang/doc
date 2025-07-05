package thread.aqs;

/**
 * 这里是测试互斥模式
 */
public class TestMyAqs {

    private static final UseMyAqs useMyAqs = new UseMyAqs();

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {

            new Thread(new Runnable() {
                public void run() {
                    useMyAqs.lock();
                    System.out.println("线程："+Thread.currentThread().getName()+"已经获得锁进入临界资源");
                    try {
                        for (int i = 0; i < 100; i++) {}
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName() + " is running");
                        useMyAqs.printQueue();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        System.out.println("线程："+Thread.currentThread().getName()+"准备释放锁");
                        useMyAqs.unlock();

                    }
                }
            }).start();
        }

        try {
            Thread.sleep(10000);
        }catch (InterruptedException e) {

        }finally {
            useMyAqs.printAcquireTimes();
        }
    }


    private static void log(String msg) {
        System.out.println(Thread.currentThread().getName() + " - " + msg);
    }

}
