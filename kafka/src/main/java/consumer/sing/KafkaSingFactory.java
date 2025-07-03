package consumer.sing;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public class KafkaSingFactory {
    public static void main(String[] args) {

    }

    static KafkaConsumer<String, String> getConsumer(boolean autoCommit,String groupId) {
        Properties kafkaCommonProperties = getKafkaCommonProperties();
        kafkaCommonProperties.put("group.id", groupId);
        kafkaCommonProperties.put("enable.auto.commit", autoCommit);

        KafkaConsumer<String,String> result = new KafkaConsumer<>(kafkaCommonProperties);
        return result;
    };

    /**
     * 定义一个静态方法
     */
    static Properties getKafkaCommonProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        /**
         * 这几为手动之后就需要自己的提交了
         */
//        props.put("enable.auto.commit", false);
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("fetch.max.bytes",52428800);
        props.put("max.partition.fetch.bytes",52428800);
        props.put("max.poll.records",500);
        props.put("fetch.max.wait.ms", "30000");  //这个是服务端的最长时间，就是客户端在这个时间内如果收集不到信息也要返回了
        props.put("auto.offset.reset", "earliest");
        return props;
    }
}
