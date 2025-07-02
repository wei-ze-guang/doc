package send.mode;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import test.KafkaProducerExample;

import java.util.Properties;

/**
 * kafka事务是可以跨分区的，幂等性是不能跨分区的
 */
public class TransactionKafka {
    public static void main(String[] args) {
        // 获取配置
        TransactionalProducerEnumSingleton instance = TransactionalProducerEnumSingleton.INSTANCE;
        Properties props = instance.getProps();

        // 配置事务相关设置
        props.put("acks", "all");  // 确保所有副本确认, 事务必须开启
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transaction-ddid");  // 设置事务ID，确保唯一
        props.put("retries", 5);  // 重试次数
        props.put("transaction.timeout.ms", 60000);  // 事务超时时间

        // 创建 Kafka 生产者实例
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        try {
            // 开始事务
            try {
                /**
                 * 初始化事务
                 */
                producer.initTransactions();

                producer.beginTransaction();
            } catch (ProducerFencedException e) {
                System.out.println("Producer fenced");
                throw new RuntimeException(e);
            }

            // 发送消息
            producer.send(new ProducerRecord<>(KafkaProducerExample.topicNew, "key", "value"));

            // 提交事务
            producer.commitTransaction();
        } catch (KafkaException e) {
            // 捕获 KafkaException 并回滚事务
            System.err.println("Error during transaction: " + e.getMessage());
            /**
             * 回滚或者中断
             */
            producer.abortTransaction();
        } finally {
            // 关闭生产者实例
//            producer.close();
        }
    }
}
