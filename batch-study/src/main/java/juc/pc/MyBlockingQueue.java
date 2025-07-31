package juc.pc;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 自己做一个生产者和消费者
 */
public class MyBlockingQueue {
    private final Queue<Integer> queue = new LinkedList<Integer>();
    private  final  int capacity;  //容量
    public MyBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized boolean produce(int source){
        while(queue.size() == capacity){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException("生产资源时候被打断了");
            }
        }
        this.queue.offer(source);
        System.out.println("Produced " + source);
        notifyAll();
        return true;
    }

    public synchronized int consume(){
        while(queue.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException("获取资源时候被打断");
            }
        }
        Integer poll = this.queue.poll();
        notifyAll();
        System.out.println("Consumed " + poll);
        return poll;
    }
}
