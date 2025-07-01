package consumer.factory.method;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过工厂方法模式获取kakfaConsumer实例
 */
public class Test {
    public static String TOPIC = "my-topic-1";

    static volatile boolean running = true;

    static AtomicLong counter = new AtomicLong(0);

    static final int times = 10;

    public static void main(String[] args) {
        Minio1BytesKafkaConsumer minio1BytesKafkaConsumer = new Minio1BytesKafkaConsumer();
        KafkaConsumer<String, String> consumer = minio1BytesKafkaConsumer.createConsumer();

        consumer.subscribe(Collections.singletonList(TOPIC));
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(Integer.MAX_VALUE);  //这个意思是10转为毫秒
            if (records.count() > 0) {
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.key() + ":" + record.value());
                }
                //在这里同步提交
                consumer.commitSync();
            }
            else{
                /**
                 * 拉取到空消息的次数
                 */

                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    System.out.println("线程被打断了");
                }
            }
            counter.incrementAndGet();

            if(counter.get() == times){
                running = false;
                consumer.close();
            }

        }
    }

}
