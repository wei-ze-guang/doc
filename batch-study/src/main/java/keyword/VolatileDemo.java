package keyword;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VolatileDemo {
    static private List<String> list = new ArrayList<>();

    static private volatile boolean flag = false;

    public static void main(String[] args) throws InterruptedException {

        new readArray().start();
        Thread.sleep(1000);

        new writeArray().start();
    }

    static class readArray extends Thread {

        Random rand = new Random();

        @Override
        public void run() {
            int size = list.size();

            while (!flag) {

            }
            System.out.println("看到flag为true，程序结束");
        }
    }
    static class writeArray extends Thread {


        @Override
        public void run() {
            flag = true;
            for (int i = 0; i < 100; i++) {
                flag = true;
            }
            System.out.println("第一遍已经设置已经结束");
            try {
                System.out.println("休眠");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                for (int i = 0; i < 100; i++) {
                    flag = true;
                }
            }
            System.out.println("结束");
        }
    }
}
