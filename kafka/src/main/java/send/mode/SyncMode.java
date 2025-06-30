package send.mode;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 同步发送模式
 */
public class SyncMode {
    public static void main(String[] args) {

       test2();
    }

    /**
     * 方法发
     */
    static void test1(){
        InnerSingletonProducer producer = InnerSingletonProducer.getInstance();
        List<Future<RecordMetadata>> futures = new LinkedList<Future<RecordMetadata>>();
        for (int i = 0; i < 100; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
            try{
                try{
                    Future<RecordMetadata> send = producer.send(String.valueOf(i));
                    try {
                        /**
                         * 这样子的话会堵塞线程，会变为什么样子呢，就是这个ProducerBatch 一直只能获取到一条数据发送出去，效率很低
                         * 因为这里在发送第一条的是时候主线成就被堵塞了，有两种方法
                         * 1  使用线程是发送和获取数据
                         */
                        RecordMetadata recordMetadata = send.get();
                        System.out.println(recordMetadata);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                };
            }catch(RuntimeException e) {
                e.printStackTrace();
            }
        }
        producer.close();
    }

    static void test2(){
        /**
         * 这种方法获取消息的话会堵塞当前线程，但是这种的话不会堵塞发送的时候，只是获取结果的时候会
         */
        InnerSingletonProducer producer = InnerSingletonProducer.getInstance();

        List<Future<RecordMetadata>> futures = new LinkedList<Future<RecordMetadata>>();
        for (int i = 0; i < 100; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
            try{
                try{
                    Future<RecordMetadata> send = producer.send(String.valueOf(i));
                    futures.add(send);
                }catch (RuntimeException e){
                    e.printStackTrace();
                };

            }catch(RuntimeException e) {
                e.printStackTrace();
            }
        }
        /**
         * 他内部会堵塞线程的，会调用flush
         */
        producer.close();
        futures.forEach(i ->{
            try {
                RecordMetadata recordMetadata = i.get();
                System.out.println("同步接收信息:"+recordMetadata.offset());
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    static void test3Flush(){
        /**
         * 直接调用flush发送，调用这个之后可以不用close()
         */
    }
}
