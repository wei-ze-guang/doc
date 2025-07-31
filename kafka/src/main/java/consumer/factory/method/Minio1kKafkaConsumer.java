package consumer.factory.method;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

/**
 * 下面是1K的
 * 工厂方法模式
 */
class Minio1kKafkaConsumer implements KafkaConsumerInterface {
    private KafkaConsumer<String, String> consumer;

    private static String groupId = "minio-1byte";

    @Override
    public KafkaConsumer<String, String> createConsumer() {
        /**
         * 只能通过接口名调用
         */
        Properties properties = KafkaConsumerInterface.getKafkaCommonProperties();

        properties.put("group.id", groupId);
        /**
         * 这个最重要
         */
        properties.put("fetch.min.bytes", 1024);
        return new KafkaConsumer<>(properties);
    }
}
