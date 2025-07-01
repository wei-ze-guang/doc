package consumer.factory.method;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

/**
 * 工厂方法模式获取kakfaConsumer类，每一个工厂生产不同类型的消费类型
 * 不像简单工厂模式，他是一个实现类代表一个工厂
 */
public interface KafkaConsumerInterface {
    KafkaConsumer<String, String> createConsumer();

    /**
     * 定义一个静态方法
     */
    static Properties getKafkaCommonProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        /**
         * 这几为手动之后就需要自己的提交了
         */
        props.put("enable.auto.commit", false);
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

