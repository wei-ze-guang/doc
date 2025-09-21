package collection;

import org.junit.Test;

import java.util.Comparator;
import java.util.PriorityQueue;

public class DemoQueue {



    @Test
    public void test() {
        //  优先级队列，非堵塞,也可以自己传传进去一个从comparator 接口,你不实现这个比较器接口的他就使用主机的，优先级队列必须能够保证内部能够比较
        //  比较器接口的返回值必须能够是 自反性、对称性、传递性
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<Integer>(10);

        PriorityQueue<Integer> priorityQueue1 = new PriorityQueue<>(20,new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1,o2);
            }
        });

        PriorityQueue<Integer> queue = new PriorityQueue<>();

        // 添加元素（自然顺序，小顶堆）
        queue.offer(5);
        queue.offer(1);
        queue.offer(3);
        queue.offer(3);

        System.out.println("队列内容: " + queue);

        // 依次取出（不会阻塞，取不到就返回 null）
        System.out.println("poll: " + queue.poll()); // 1
        System.out.println("poll: " + queue.poll()); // 3
        System.out.println("poll: " + queue.poll()); // 5
        System.out.println("poll: " + queue.poll()); // null（非阻塞，直接返回）
    }

}

