package thread.aqs;

/**
 * 这里是测试互斥模式
 */
public class TestShareMyAqs {

    private static final UseShareMyAqs useShareMyAqs = new UseShareMyAqs(10);

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {

            new Thread(new Runnable() {
                public void run() {
                    useShareMyAqs.lockShare(3);
                    System.out.println("线程："+Thread.currentThread().getName()+"已经获得锁进入临界资源");
                    try {
                        for (int i = 0; i < 100; i++) {}
                        Thread.sleep(10);
                        System.out.println(Thread.currentThread().getName() + " is running");
                        useShareMyAqs.printQueue();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        System.out.println("线程："+Thread.currentThread().getName()+"准备释放锁");
                        useShareMyAqs.unLockShare(2);

                    }
                }
            }).start();
        }

        try {
            Thread.sleep(10000);
        }catch (InterruptedException e) {

        }finally {
            useShareMyAqs.printAcquireShareTimes();
        }
    }


    private static void log(String msg) {
        System.out.println(Thread.currentThread().getName() + " - " + msg);
    }

}

