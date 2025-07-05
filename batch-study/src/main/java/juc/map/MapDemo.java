package juc.map;

import juc.CommonPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Map  的实现类比较多  HashMap,LinkedHashMap,TreeMaP
 * HashMap 是无序的，LinkHashMap是，有序的，安全插入的顺序
 * TreeMap 是有序的，而且它的顺序不是“插入顺序”，而是基于 key 的“自然顺序”或你提供的 Comparator 顺序。
 */
public class MapDemo {
    /**
     * 下面是无序的
     */
//    private static final HashMap<String,String> map = new HashMap<String,String>();
    /**
     * 下面是按插入顺序的
     */
//    private static final Map<String,String> map = new LinkedHashMap<>();

    /**
     * 下面是按照这个key的Comparable接口排序的
     */
//    private static final Map<String,String> map = new LinkedHashMap<>();
    /**
     * 下面是互斥锁的
     */
//    private static final Map<String,String> map = new Hashtable<>();

    /**
     * juc下的ConCurrentHashMap
     */

    private static final Map<String,String> map = new ConcurrentHashMap<String,String>();

    private static int times = 1000;

    private static int threads = 100;

    public static void main(String[] args) {
        ExecutorService instance = CommonPool.getCachedThreadPool();
        for (int i = 0; i < times; i++) {
            int finalI = i;
            instance.submit(new Runnable() {
                public void run() {
                    final String ii = String.valueOf(finalI) ;
                    map.put(ii,ii);
                    System.out.println("put: " + ii);
                }
            });

            instance.submit(new Runnable() {
                public void run() {
                    for(Map.Entry<String,String> entry :map.entrySet() ) {
                        map.remove(entry.getValue());
                        String key = entry.getKey();
                        String value = entry.getValue();
                        System.out.println("key"+"="+key);
                        System.out.println("value"+"="+value);
                        System.out.println(map.get(key));
                    }
                }
            });
        }


        instance.shutdown();
        try {
            instance.awaitTermination(100000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
