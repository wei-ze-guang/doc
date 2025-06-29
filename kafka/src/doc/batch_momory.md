## 消息累加器 KafkaProducer 中的 消息累加器（RecordAccumulator）
消息、分区、批次（batch）、缓冲区（buffer.memory）之间的“中枢神经”。  
- 一句话总结
RecordAccumulator 是 KafkaProducer 的消息缓冲器，负责把发送到相同 topic-partition 的消息收集起来，按 batch.size 聚合成 batch，放入内存缓冲池，等时机成熟再批量发送出去。  
```java
KafkaProducer
  └── RecordAccumulator  ←————→ 线程安全的缓冲池结构
        └── Map<topic-partition, Deque<ProducerBatch>>
                      ├── batch1（未满）
                      ├── batch2（已满，待发）
                      └── ...

``` 
| 元素                  | 含义                                                   |
| ------------------- |------------------------------------------------------|
| `RecordAccumulator` | KafkaProducer 内部维护的消息累加器                             |
| `topic-partition`   | Kafka 按分区路由消息，每个分区独立维护一个消息队列    ,这个队列存储producerBatch |
| `ProducerBatch`     | 每个 batch 是写入同一个分区的消息集合（最大为 `batch.size`）             |
| `Deque`             | 对每个分区是一个双端队列，维护多个 batch（先进先出）                        |
---  
### 整个流程  
你调用 producer.send(record),KafkaProducer 把 record 交给 RecordAccumulator 它会按 record 的 topic-partition 找到对应队列：
- 如果最后一个 batch 没满，就加进去
- 如果满了，就新建一个 batch
- 等待满足以下任一条件：
  - batch 满了（batch.size）
  - linger.ms 到时间了
  - flush() 或 send().get() 触发发送
Sender 线程从 RecordAccumulator 拉取 batch，打包发给 Broker  
---  
| 配置项                | 和 RecordAccumulator 的关系    |
| ------------------ | -------------------------- |
| `batch.size`       | 控制单个 `ProducerBatch` 的最大容量 |
| `buffer.memory`    | 控制整个 RecordAccumulator 总容量 |
| `linger.ms`        | 控制最长等待时间，不满也强制发送           |
| `compression.type` | 会影响 batch 的压缩存储            |

###  重要参数 max.block.ms  默认60000 ，60秒
- 假如消息来的非常快，消息累加器装不下，怎么办呢？  
  - 消息累加器会等待60，这60秒内如果发送出去一些数据，缓冲区也就是消息累加器看一下这个时间段没有消息发送出去  
  - 如果发送出去了缓冲区有空间了，就不会抛出异常 TimeoutException，抛出这个异常说明接收消息和发送消息严重不匹配  
  - 如何理解呢 kafkaProducer的Send方法，一般缓冲区满了，这个方法就会进入堵塞，60秒内如果添加不到缓冲区就抛出异常  
### producerBatch 大小，一般是固定的，但是当一条消息的大小都比这个大的时候，这个producerBatch 就是会根据这一条消息的大小实际大小来创建  
