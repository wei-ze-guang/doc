package debug;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class DebugDemo {
    private static final ReentrantLock lock = new ReentrantLock();

    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();


    public static void main(String[] args) {

        System.out.println("程序开始");

       for (int i = 0; i < 10; i++) {
           new DemoThread().start();
       }

        System.out.println("程序结束");

    }

   static private void getData(){
        lock.lock();
        try {
            System.out.println("GetData");
        }finally {
            lock.unlock();
        }
    }

    static class DemoThread extends Thread{
        @Override
        public void run() {
            getData();
        }
    }
}
