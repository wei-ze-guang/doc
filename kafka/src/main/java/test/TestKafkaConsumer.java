package test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 简单测试一下拉取小区，分区数量和消费者数的情况
 */

/**
 * | 参数名                     | 说明                                  | 默认值            |
 * | ----------------------- | ----------------------------------- | -------------- |
 * | `fetch.min.bytes`       | 消费者单次请求拉取的最小数据量（字节）。未达到这个值，会等待更多数据。 | 1              |
 * | `fetch.max.bytes`       | 消费者单次请求拉取的最大数据量（字节）。超过这个值会分多次拉取。    | 52428800（50MB） |
 * | `fetch.max.wait.ms`     | 消费者拉取请求等待服务器返回的最长时间（毫秒）。            | 500            |
 * | `max.poll.records`      | 单次 `poll()` 拉取的最大消息条数。              | 500            |
 * | `max.poll.interval.ms`  | 两次 `poll()` 之间的最大允许间隔，超过会触发再均衡。     | 300000（5分钟）    |
 * | `session.timeout.ms`    | 心跳超时，消费者被判定死亡前的最长等待时间（毫秒）。          | 10000 - 30000  |
 * | `heartbeat.interval.ms` | 消费者发送心跳给协调器的间隔时间（毫秒）。               | 3000           |
 */
@Slf4j
public class TestKafkaConsumer {

    private static String topic = "my-topic-1";

    private static String groupId = "group-t";

    private static AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = getConsumer();
        /**
         * 下面可以是集合形式，也可以是正则表达式
         */
        consumer.subscribe(Collections.singleton(topic));
        System.out.println("Subscribed to topic " + topic);
        ConsumerRecords<String, String> records = null;
        while (running.get()) {
            try {
                /**
                 * 下面这个参数是
                 * 如果在规定时间内，服务端返回数据，这里也返回，但是如果客户端还没返回这里会抛出异常，
                 * 如果一直消费这里设置为无限大，一直拉取就好了
                 */
                records = consumer.poll(100);

                if (records!= null && !records.isEmpty()) {
                    consumer.commitSync(); // 推荐 sync 保证提交成功
                }
                System.out.println("拉取到数量:"+records.count());
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("value:"+record.value());
                    System.out.println("key:"+record.key());
                    System.out.println("分区"+record.partition());
                    log.info("偏移量offset::"+record.offset());
                }

            }catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }finally {
                if(records!=null && records.count()>0)
                    running.set(false);
            }
        }


    }

    static Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");  // Kafka 集群地址
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);  // 消费者组 ID
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // 键的反序列化器
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // 值的反序列化器

        /**
         * 延迟性和吞吐量下面很重要，延迟性要求高设置为1最好，要求吞吐量的话设置越高最好，
         */
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);  // 不要设置过高,消费者每次最少拉取多少字节的数据才返回，默认1 ,这个数据才是最重要的

        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 52428800);//默认 50BM。一次拉取数据的最大总和 ，这个不是那么重要

        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 104857600);  //单个分区拉取的最字节数量字节，有能会被突破

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);  //单次拉取的最大信息条数，是条数

        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);//毫秒  	心跳超时，消费者被判定死亡前的最长等待时间（毫秒）

        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000); //消费者发送心跳给协调器的间隔时间（毫秒）默认3000

        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 30000);//	消费者拉取请求等待服务器返回的最长时间（毫秒）。//  默认500，这个是服务端的时间
        /**
         * Kafka 消费者在启动时，会先去 协调器（coordinator） 查询这个消费者组对应分区的 已提交的 offset（也叫提交位点）。
         * 如果找到了有效的 offset，就从这个 offset 开始消费（即“接着上次消费的位置”）。
         * 如果没找到有效的 offset（比如这个消费者组第一次消费，或者 offset 被 Kafka 清理掉了），才会根据 auto.offset.reset 配置决定从哪里开始消费：
         * earliest：从最早保留的消息开始消费。
         * latest：从最新消息开始消费（跳过历史消息）。
         * none：抛出异常，提示没有可用 offset。
         */
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // 如果没有偏移量，默认从最早的消息开始消费

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        return props;
    }
    static KafkaConsumer<String, String> getConsumer() {
        Properties props = getProperties();
        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(props);
        return consumer;
    };
}
