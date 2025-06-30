package test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * | 配置项 | 说明 | 通俗理解 | 示例值 |
 * |--------|------|----------|--------|
 * | `bootstrap.servers` | Kafka 集群地址 | 快递总公司地址 | `localhost:9092` |
 * | `key.serializer` | Key 的序列化器 | 快递编号怎么打包 | `org.apache.kafka.common.serialization.StringSerializer` |
 * | `value.serializer` | Value 的序列化器 | 快递内容怎么打包 | `org.apache.kafka.common.serialization.StringSerializer` |
 * | `acks` | 确认级别 | 快递是否签收才算成功 | `0` / `1` / `all` |
 * | `retries` | 失败重试次数 | 快递员送不出去要不要多送几次 | `3`（默认 0） |
 * | `batch.size` | 批次大小（字节） | 一次打包多少快递再发 | `16384` |
 * | `linger.ms` | 批次等待时间（毫秒） | 等一会儿凑一批发 | `1` |
 * | `buffer.memory` | 缓冲区大小 | 等待发出的快递的仓库容量 | `33554432` |
 * | `compression.type` | 压缩算法 | 打包快递用不用压缩 | `none` / `gzip` / `snappy` / `lz4` |
 * | `client.id` | 客户端 ID | 快递客户编号 | `producer-client-1` |
 * | `enable.idempotence` | 幂等保障 | 防止重复发快递 | `true`（建议开启） |
 */
public class KafkaProducerExample {
    public static String topicNew = "my-topic-1";
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties props = new Properties();
//        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//
//        try (AdminClient admin = AdminClient.create(props)) {
//            NewTopic topic = new NewTopic(topicNew, 3, (short) 1); // 3个分区，1个副本
//            admin.createTopics(Collections.singleton(topic)).all().get();
//            System.out.println("Topic created successfully!");
//        }

//        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("client.id", "p-1");  //生产者id

        props.put("interceptor.classes", MyProducerInterceptor.class.getName());  //生产者拦截器

        //props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");  一定要全限定名
        props.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class.getName());  //这种更好
        //props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");  全限定名字
        props.put("value.serializer",org.apache.kafka.common.serialization.StringSerializer.class.getName());
        /**
         * - acks（Acknowledgement）决定 Kafka 服务器端对消息写入的确认策略：
         *   - acks=0：生产者不等待服务器确认，发送了就算成功（对应“发送了不管”）。
         *   - acks=1：领导者写入成功即确认，副本同步不等待。
         *   - acks=all或 acks=-1：所有同步副本确认后才返回成功，最安全。
         */
        props.put("acks", "all");  // 0/1/all 或者还是0/-1/all
        props.put("retries", 3);  //失败重试次数，默认为0
        props.put("retry.backoff.ms", 200);  // 每次重试间隔200毫秒 ,默认100

        props.put("batch.size", 16384);  //批次大小
        props.put("linger.ms", 1);  // 批次等待时间

        props.put("buffer.memory", 33554432); //缓冲区大小

        props.put("compression.type", "gzip");  // none / gzip / snappy / lz4 压缩算法
        props.put("enable.idempotence", true);  // 幂等性

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 10; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
            Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topicNew, 0, "key-" + i, "value-" + i));//指定分区之后，key不会计算分区
            producer.send(new ProducerRecord<>(topicNew,"key-" + i, "value-" + i));  //这样子会计算分区
            producer.send(new ProducerRecord<>(topicNew,null, "value-" + i));  //轮询
        }
        producer.close();
    }
}