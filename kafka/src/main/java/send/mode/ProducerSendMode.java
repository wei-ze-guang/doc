package send.mode;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.internals.ProducerMetadata;
import test.KafkaProducerExample;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * 用来演示三种发送方式的生产者
 */
public class ProducerSendMode {
    private KafkaProducer<String,String> kafkaProducer;

    //采用双重检查方法获取
    private static volatile ProducerSendMode producer;

    private ProducerSendMode(){
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("client.id", "p-1");  //生产者id
        props.put("interceptor.classes", test.MyProducerInterceptor.class.getName());  //生产者拦截器
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
        this.kafkaProducer = producer;
    }

    /**
     * DCL创建
     * @return
     */
    public static  ProducerSendMode getInstance(){
        if(producer == null){
            synchronized (ProducerSendMode.class){
                if(producer == null){
                    producer = new ProducerSendMode();
                    return producer;
                }
            }
        }
        return producer;
    }

    public Future<ProducerMetadata> send(String value){
        /**
         * key  是null。轮询方式
         */
        Future send = kafkaProducer.send(new ProducerRecord<String,String>(KafkaProducerExample.topicNew,null,value));
        return send;
    }

    public void close() {
        kafkaProducer.close();
    }

}
