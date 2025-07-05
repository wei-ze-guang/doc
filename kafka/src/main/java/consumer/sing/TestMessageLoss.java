package consumer.sing;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

/**
 * 演示消息丢失，一般是因为开启了自动提交，offset已经提交成功但是消息没处理完，
 * 下次拉取到了是以已经提交的数据
 */
public class TestMessageLoss {
    private static AtomicBoolean running = new AtomicBoolean(true);

    private static LongAdder counter = new LongAdder();

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = KafkaSingFactory.getConsumer(true, "group-new");
        consumer.subscribe(Collections.singleton("my-topic-1"));

        while (running.get()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(100);
                if (records.count() > 0) {
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.println("主题："+record.topic() );
                        System.out.println("分区："+record.partition() );
                        System.out.println("偏移:"+record.offset());

                        counter.increment();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                consumer.commitSync();
                if(counter.longValue() > 100){
                    running.set(false);
                }
            }
        }
    }
}
