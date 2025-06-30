package test;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 发送拦截器，需要在生产者配置中配置
 * props.put("interceptor.classes", "com.example.MyProducerInterceptor");
 */
public class MyProducerInterceptor implements ProducerInterceptor<String, String> {

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        // 这里可以修改消息，比如加个前缀
        String newValue = "[Intercepted] " + record.value();
        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(), record.key(), newValue, record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            System.out.println("消息发送成功，offset=" + metadata.offset());
        } else {
            System.out.println("消息发送失败：" + exception.getMessage());
        }
    }

    @Override
    public void close() {
        System.out.println("生产者拦截器关闭");
    }

    @Override
    public void configure(Map<String, ?> configs) {
        System.out.println("生产者拦截器初始化配置");
    }
}

