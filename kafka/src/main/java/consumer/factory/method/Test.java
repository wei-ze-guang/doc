package consumer.factory.method;

import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过工厂方法模式获取kakfaConsumer实例
 * 三种方法消费消息 订阅方法 通配符方法，还有assign方法
 */
public class Test {
    public static String TOPIC = "my-topic-1";

    static volatile boolean running = true;

    static AtomicLong counter = new AtomicLong(0);

    static final int times = 10;

    public static void main(String[] args) {
//        Minio1BytesKafkaConsumer minio1BytesKafkaConsumer= new Minio1BytesKafkaConsumer();
//        KafkaConsumer<String, String> consumer = minio1BytesKafkaConsumer.createConsumer();
//
//        List<PartitionInfo> partitionInfo = getPartitionInfo(TOPIC, consumer);
//        if (partitionInfo.size() > 0) {
//            System.out.println("PartitionInfo: " + partitionInfo);
//        }
        testAssign();
    }

    /**
     * 测试assign直接订阅某个主题的某个分区
     * seek() 方法只能和 assign() 一起使用，不能配合 subscribe() 使用。
     * 这种订阅可以一次指定订阅多个分区，需要手动维护offset
     * kafka不会维护主题的offset ，这里也不需要groupId，需要自己维护offset
     * 使用 assign() 不能触发分区 rebalance，你必须自己维护好分区。
     * 不支持自动负载均衡。
     * 不需要 group.id 也能用，但如果你想提交 offset 仍然需要。
     */
    static void testAssign(){
        Minio1BytesKafkaConsumer minio1BytesKafkaConsumer= new Minio1BytesKafkaConsumer();
        KafkaConsumer<String, String> consumer = minio1BytesKafkaConsumer.createConsumer();
        TopicPartition topicPartition = getTopicPartition();

        consumer.assign(Collections.singletonList(topicPartition));
        consumer.seek(getTopicPartition(),0L);
        while (running) {
            ConsumerRecords<String, String> poll = consumer.poll(1000L);

            if (poll != null && poll.count() > 0) {
                for (ConsumerRecord<String, String> record : poll) {
                    System.out.println(record.key() + ":" + record.value());
                }
            }

        }

    }

    static TopicPartition getTopicPartition(){
        /**
         * 如果使用assign方法消费信息的话需要使用这个
         * 这个需要处理一个问题就是如何知道这个在主题有什么分区呢
         * 使用消费者的PartitionsFor方法获取这个主题的信息
         */
        return new TopicPartition(TOPIC, 0);
    }

    /**
     * 获取主题的所欲分区，包括AR ISR OSR
     * @param topic
     * @param consumer
     * @return
     */
    static List<PartitionInfo> getPartitionInfo(@NonNull  String topic, KafkaConsumer<String, String> consumer) {
        List<PartitionInfo> partitionInf = consumer.partitionsFor(topic);
        return partitionInf;
    }

}
