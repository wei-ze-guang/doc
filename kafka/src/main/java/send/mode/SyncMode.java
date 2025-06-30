package send.mode;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * 同步发送模式
 */
public class SyncMode {
    public static void main(String[] args) {
       InnerSingletonProducer producer = InnerSingletonProducer.getInstance();
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
}
