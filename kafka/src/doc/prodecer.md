# Kafka Producer 常用配置详解（通俗 + 示例）

## 🎯 总览表

| 配置项 | 说明                                                                       | 通俗理解     | 示例值                                                      |
|--------|--------------------------------------------------------------------------|----------|----------------------------------------------------------|
| `bootstrap.servers` | Kafka 集群地址                                                               | 快递总公司地址  | `localhost:9092`                                         |
| `key.serializer` | Key 的序列化器                                                                | 快递编号怎么打包 | `org.apache.kafka.common.serialization.StringSerializer` |
| `value.serializer` | Value 的序列化器                                                              | 快递内容怎么打包 | `org.apache.kafka.common.serialization.StringSerializer` |
| `acks` | 确认级别                                                                     | 快递是否签收才算成功 | `0` / `1` / `all`                                        |
| `retries` | 失败重试次数                                                                   | 快递员送不出去要不要多送几次 | `3`（默认 0）                                                |
| `batch.size` | 批次大小（字节）                                                                 | 一次打包多少快递再发 | `16384`                                                  |
| `linger.ms` | 批次等待时间（毫秒）                                                               | 等一会儿凑一批发，如果为0，就失去批量发送的优势，来一个发一个 | `1`                                                      |
| `buffer.memory` | 缓冲区大小                                                                    | 等待发出的快递的仓库容量 | `33554432`                                               |
| `compression.type` | 压缩算法                                                                     | 打包快递用不用压缩 | `none` / `gzip` / `snappy` / `lz4`                       |
| `client.id` | 客户端 ID                                                                   | 快递客户编号   | `producer-client-1`                                      |
| `enable.idempotence` | 幂等保障                                                                     | 防止重复发快递  | `true`（建议开启）                                             |  
| `retry.backoff.ms` | 重试间隔时间                                                                   |          | 默认100毫秒                                                  |  
| `max.request.size` | 单体网络请求的最大发送大小，如果消息超过这个会抛出异常                                              |          | 默认100毫秒                                                  |  
| `delivery.timeout.ms ` | 消息发送的最大等待时间，send()方法开始算即从生产者发送消息开始，到收到 Kafka 集群的成功确认，最多允许的时间。默认2秒 -1表示无限 |          | 2秒                                                       |
| `request.timeout.ms ` | 生产者发送请求后等待 Kafka broker 响应的最大时间。这个是发送网络请求开始算，和上面不同                       |          |                                                 |  
### 容易漏掉的参数  
- 容易被忽视的配置 包括：
  - linger.ms：增加等待时间，批量发送消息。
  - max.in.flight.requests.per.connection：控制请求并发。
  - max.request.size：设置请求的最大字节数，避免过大的请求失败。
  - retry.backoff.ms：重试的间隔时间，控制重试频率。
####  max.in.flight.requests.per.connection这个参数需要重点了解，他跟顺序性和事务acks和事务有很大关系，这时候最好设置为1
max.in.flight.requests.per.connection 在 enable.idempotence=true 启用时，
会被 Kafka 自动设置为 1。这是因为，启用幂等性时，Kafka 需要确保消息顺序性，避免在并发情况下发生消息乱序或重复提交的情况。
### 这里补充几点注意事项：
- 重试是自动的，你不需要自己写循环，KafkaProducer 内部帮你处理。
- 发送超时时间：重试不能无限等待，delivery.timeout.ms 会限制整个发送（包括重试）最长等待时间，超过就失败。
- 幂等性：开启幂等模式 (enable.idempotence=true) 能保证重试不会造成消息重复写入。
- 无重试或重试次数为0时，发送失败会立即返回错误。  
---  

- acks（Acknowledgement）决定 Kafka 服务器端对消息写入的确认策略：
  - acks=0：生产者不等待服务器确认，发送了就算成功（对应“发送了不管”）。
  - acks=1：领导者写入成功即确认，副本同步不等待。
  - acks=all或 acks=-1：所有同步副本确认后才返回成功，最安全。

- 幂等性（idempotence）
  - 幂等性是生产者端配置 enable.idempotence=true，保证即使消息重试，Kafka 也不会重复写入相同消息（避免重复消费）。
  - 幂等性依赖于 acks=all 和重试机制协同工作。
  - 幂等性和同步/异步发送是不同维度的概念，幂等性关注“消息不重复”，发送方式关注“发送时机和等待”。

- 一致性
  - 一致性指消费者读取到的数据是否符合预期，是否有丢失或重复。
  - acks=all + 幂等性能提高消息持久化的一致性。  
- batch.size（批次大小）
含义：Producer 每个分区在内存中可以缓存的消息批次大小（字节数）。默认值：16384（16KB）
  - 作用：  
    - Kafka 生产者会尽量将多条消息累积成一个批次（batch）发送，减少网络请求次数，提高吞吐量。
批次大小设置越大，可能会积累更多消息，网络利用率更高，但会增加延迟（因为要等满批次或超时才发送）。
  - 注意：
这是针对每个分区的批次大小，多个分区并行会有多个批次缓冲。
---  
- linger.ms（批次等待时间）  
- 含义：生产者在发送批次前的最大等待时间（毫秒）。默认值：0（表示消息生成后立即发送）
  - 作用：
生产者发送消息时，如果批次未达到 batch.size，会等待最多 linger.ms 毫秒，看看是否能凑满批次，提高批量发送效率。
设置为1表示最多等待1毫秒，减少请求次数，换取一点点延迟。
  - 调优建议：
    - 延迟敏感场景可以设置0，保证消息即时发送。
    - 追求吞吐量场景可适当增加，比如几毫秒。
    - 消费者端还需结合事务或消费幂等处理，才能保证端到端一致性。
---  
- buffer.memory（缓冲区大小） 默认值：33554432（32MB）
-含义：生产者总内存缓冲区大小，单位字节。
  - 作用：
Producer 内部维护一个缓冲区用来缓存待发送的消息，避免每条消息都马上发出。
如果缓冲区满了，发送线程会阻塞或抛异常（取决于配置）。
  - 注意：
该缓冲区用于所有分区的消息缓存，和 batch.size 结合使用。  
---  
- 关系总结
  - batch.size 和 linger.ms 共同决定了消息批次的大小和发送频率。
  - buffer.memory 决定生产者可用的内存缓存总量，影响生产者在高负载时的表现。
  - 适当调大 batch.size 和 linger.ms 可以提升吞吐量，但会增加发送延迟。
  - 配合压缩和合适的 acks 设置，可以平衡性能和可靠性。  
---  

- 类似的常用参数及说明  

| 参数名                   | 含义           | 作用                                                           |
| --------------------- | ------------ | ------------------------------------------------------------ |
| `compression.type`    | 压缩类型         | 对消息批次压缩，支持 `none`, `gzip`, `snappy`, `lz4`, `zstd`，减少网络传输和存储 |
| `max.request.size`    | 单个请求最大大小（字节） | 限制发送消息大小，默认 1MB，防止发送超大消息导致失败                                 |
| `request.timeout.ms`  | 请求超时时间       | 生产者发送请求等待 broker 响应的超时时间，默认 30秒                              |
| `acks`                | 服务器应答策略      | 确保消息发送成功的可靠性设置，如 `0`, `1`, `all`                             |
| `retries`             | 重试次数         | 失败重试次数，结合幂等性和幂等生产者，保证消息不会丢失                                  |
| `delivery.timeout.ms` | 发送消息的最大允许时间  | 包含所有重试时间，超过则报错                                               |


## 🧪 示例 Java 代码

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerExample {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "all");
        props.put("retries", 3);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("compression.type", "gzip");
        props.put("enable.idempotence", true);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>("my-topic", "key-" + i, "value-" + i));
        }
        producer.close();
    }
}
