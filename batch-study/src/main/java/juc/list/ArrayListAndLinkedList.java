package juc.list;

import juc.CommonPool;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 现实ArrayList和LinkedList
 */
public class ArrayListAndLinkedList {
    /**
     * 读写列表的话只有写的时候需要获取互斥锁，写的时候会拷贝一个副本，读的时候不用锁，可以同时读和写，但是会出现弱一致性
     */
//    private static final CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

    /**
     * 这个线程安全读写都是互斥锁，效率低
     */
    private static final List<String> list = new Vector<String>();

    /**
     * 使用下列非线程安全的会异常 java.util.ConcurrentModificationException  这个是非受检异常
     */
    //private static final LinkedList<String> list = new LinkedList<>();
    //private static final ArrayList<String> list = new ArrayList<>();

    static final int times = 100;

    static final int threads = 100;

    public static void main(String[] args) {

        ExecutorService instance = CommonPool.getInstance();

        for (int i = 0; i < threads; i++) {
            int finalI = i;
            instance.execute(() ->{
                    final int ii = finalI;
                    list.add(String.valueOf(ii));
                });

            instance.execute(() ->{
                list.forEach(System.out::println);
            });
        }

        instance.shutdown();
        try {
            instance.awaitTermination(1000L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
