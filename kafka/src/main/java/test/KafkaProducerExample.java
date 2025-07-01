package test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    public static void main(String[] args) {

        Properties props = new Properties();
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

        /**
         * he message is 100106 bytes when serialized which is larger than 4096, which is the value of the max.request.size configuration.
         * 会抛出类似异常
         */
        props.put("max.request.size", 10485760);  // 10MB,单词请求的最大大小

        props.put("batch.size", 16384);  //批次大小
        props.put("linger.ms", 100);  // 批次等待时间，如果为0，就会失去 批量发送的优势，因为来一个就发一个，他不会等待

        props.put("buffer.memory", 33554432); //缓冲区大小

        props.put("compression.type", "gzip");  // none / gzip / snappy / lz4 压缩算法
        /**
         * 开启了幂等性之后
         * max.in.flight.requests.per.connection 他会自动设置为一，你设置了会被覆盖
         */
        props.put("enable.idempotence", true);  // 幂等性

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 创建多个自定义消息头
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("header_key1", "header_value1".getBytes()));
        headers.add(new RecordHeader("header_key2", "header_value2".getBytes()));
        for (int i = 0; i < 10000; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
//            Future<RecordMetadata> send = producer.send(new ProducerRecord<>(
//                    topicNew,
//                    null,
//                    Instant.now().toEpochMilli(),
//                     "key",
//                    "value-",
//                    headers
//            ));//指定分区之后，key不会计算分区
            try {
                producer.send(new ProducerRecord<>(topicNew,"key-" + i, getBitString()));  //这样子会计算分区
//                producer.send(new ProducerRecord<String,String>(topicNew,null, "value-" + i, null));  //轮询
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        producer.close();
    }

    static String getBitString(){
        StringBuilder sb = new StringBuilder();
        String base = "0123456789";
        sb.append(base.repeat(10000));
        return sb.toString();
    }
}