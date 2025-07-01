package send.mode;


import java.util.Properties;

/**
 * 单体模式创建一个生产者
 */
public enum TransactionalProducerEnumSingleton {
    /**
     * 由于这个KafkaProducer 设置之后不能再修改，这里修改为返回一个Properties
     */
    INSTANCE;

    private  Properties props;

    private TransactionalProducerEnumSingleton() {
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
//        props.put("acks", "all");  // 0/1/all 或者还是0/-1/all
//        props.put("retries", 3);  //失败重试次数，默认为0
        props.put("retry.backoff.ms", 200);  // 每次重试间隔200毫秒 ,默认100

        props.put("batch.size", 16384);  //批次大小
        props.put("linger.ms", 1);  // 批次等待时间

        props.put("buffer.memory", 33554432); //缓冲区大小

        props.put("compression.type", "gzip");  // none / gzip / snappy / lz4 压缩算法
        props.put("enable.idempotence", true);  // 幂等性

        this.props = props;
    }

    public Properties getProps() {
        return props;
    }
}
