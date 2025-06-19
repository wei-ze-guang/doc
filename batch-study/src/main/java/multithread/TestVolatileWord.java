package multithread;

/**
 * 用来测试volatile关键字
 */
public class TestVolatileWord {

    //  加了volatile
    private static volatile int a = 1;

    public static void main(String[] args) {

        new Thread(new OneThreadWord()).start();
        new Thread(new TwoThreadWord()).start();

    }

    static class OneThreadWord implements Runnable {
        @Override
        public void run() {
            int count = 0;
            while (a == 1) {
                count++;
            }
            System.out.println("a != 1，退出了 while1，循环了 " + count + " 次");

            count = 0;
            while (a == 2) {
                count++;
            }
            System.out.println("a != 2，退出了 while2，循环了 " + count + " 次");

            count = 0;
            while (a == 3) {
                count++;
            }
            System.out.println("a != 3，退出了 while3，循环了 " + count + " 次");

            count = 0;
            while (a == 4) {
                count++;
            }
            System.out.println("a != 4，退出了 while4，循环了 " + count + " 次");
        }
    }


    static  class TwoThreadWord implements Runnable {

        @Override
        public void run() {
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 2;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 3;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            a = 4;
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            a = 5;   //注意这里最后操作结果是5

            System.out.println("操作线程结束");
        }
    }

}


