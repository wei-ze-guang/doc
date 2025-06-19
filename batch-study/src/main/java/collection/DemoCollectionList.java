package collection;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 这里展示List相关接口
 */
public class DemoCollectionList {

    /**
     *   private static final int DEFAULT_CAPACITY = 10; 这是他源码，默认10，1.5被或者2扩容
     */

//    static final Collection c = new ArrayList();
//    static final Collection e = new LinkedList();
//    static final Collection d = new HashSet();  //唯一的，没有重复值

    static final List f = new ArrayList();
    static final Queue e = new LinkedList();   //他也实现了queue接口，当然也实现了List接口

    static final Set d = new HashSet();  //唯一的，没有重复值，基于哈希表


    /**
     * 这个是一个线程安全的，他是juc包下的
     */
    static CopyOnWriteArrayList c = new CopyOnWriteArrayList();
    /**
     *     /**
     *      * 保证元素 排序，元素按自然顺序（实现 Comparable）或自定义 Comparator 排序。
     *      * 保证元素 排序，元素按自然顺序（实现 Comparable）或自定义 Comparator 排序。
     *      * 元素唯一，判断重复基于排序规则（compareTo 或 Comparator.compare），不是简单的 equals。
     *      */
    static final Set h = new TreeSet();  //红黑树，有序

    static final List a =  new Vector<Integer>();

    static final int threads = 100;

    /**
     * 这个是一个线程安全的，他是juc包下的
     */
    static final CountDownLatch countDownLatch = new CountDownLatch(threads);

    static final int forTimes = 10;

    public static void main(String[] args) throws InterruptedException {
        /**
         * 核心方法
         * add(), remove(), contains(), iterator(),clear(),isEmpty(),size()  上面这些方法基本有返回值
         */
        /**
         *以下三个实现是非线程安全的
         */

        for (int i = 0; i < threads; i++) {
            new Thread(new addObject()).start();

        }

//        countDownLatch.await();  //这里不会出现虚假唤醒

        for (int i = 0; i < threads; i++) {
            new Thread(new removeObject()).start();
            new Thread(new getObject()).start();
        }

    }

    /**
     * Exception in thread "Thread-263" java.util.ConcurrentModificationException
     * 	at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1013)
     * 	at java.base/java.util.ArrayList$Itr.next(ArrayList.java:967)   会发生这个报错
     */

    /**
     * ArrayList 的迭代器是快速失败（fail-fast）机制：
     * ArrayList 内部有一个 modCount（修改次数）
     * 每次你调用 add() / remove()，它都会递增 modCount
     * 当你通过迭代器 iterator.next() 遍历时，它会检查 modCount 是否和初始一致
     * 如果中途别人修改了这个 list，那 modCount 就变了
     * 检查失败就会抛出 ConcurrentModificationException
     */

    /**
     * 这样子遍历删除就不会了，添加的话还不行
     * Iterator<String> iterator = list.iterator();
     * while (iterator.hasNext()) {
     *     String item = iterator.next();
     *     if (item.equals("b")) {
     *         iterator.remove(); // ✅ 正确做法，不会触发异常
     *     }
     * }
     */

    static class addObject implements Runnable {
        @Override
        public void run() {
            Long start = System.currentTimeMillis();
            for (int i = 0; i < forTimes; i++) {
                System.out.println("添加一个元素:"+i);
               c.add(i);
            }

            Long end = System.currentTimeMillis();
            System.out.println("总时间："+(end - start));
//            countDownLatch.countDown();
        }
    }
    static class removeObject implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < forTimes/2; i++) {
                System.out.println("移除一个元素:"+i);
                c.remove(i);
            }
        }
    }

    static class getObject implements Runnable {

        @Override
        public void run() {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }
        }
    }
}
/**
 * Iterator 的遍历原理和使用方式
 * 返回一个 Iterator 对象，这个对象本质上封装了遍历集合的状态（比如当前索引或者当前节点）。
 * Iterator 有三个主要方法：
 * hasNext()：判断集合中是否还有下一个元素。
 * next()：返回下一个元素，并且迭代器向前推进一步。
 * remove()（可选）：删除上一次 next() 返回的元素。
 */

/**
 * | 方法签名                                          | 作用                    | 备注         |
 * | --------------------------------------------- | --------------------- | ---------- |
 * | `void add(int index, E element)`              | 在指定位置插入元素             | 插入前自动右移    |
 * | `E get(int index)`                            | 获取指定索引的元素             | 类似数组访问     |
 * | `E set(int index, E element)`                 | 替换指定位置的元素             | 返回旧值       |
 * | `E remove(int index)`                         | 删除指定位置的元素             | 后面的会左移     |
 * | `int indexOf(Object o)`                       | 查找第一次出现的位置            | 没找到返回 -1   |
 * | `int lastIndexOf(Object o)`                   | 查找最后一次出现的位置           |            |
 * | `ListIterator<E> listIterator()`              | 返回 `ListIterator` 迭代器 | 支持双向迭代、修改等 |
 * | `ListIterator<E> listIterator(int index)`     | 从指定位置开始的迭代器           |            |
 * | `List<E> subList(int fromIndex, int toIndex)` | 子列表视图（半开区间）           | 修改视图会影响原集合 |
 */
