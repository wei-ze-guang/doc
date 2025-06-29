## ProducerRecode 类  发送的时候的对象
```java
    private final String topic;   //主题

    private final Integer partition;   //  分区
    /**
     * 消息头部
     */
    private final Headers headers;  //头部
    /**
     * key  不仅是附加信息，还用计算分区号，有key 还支持日志压缩，
     */
    private final K key;
    /**
     * 消息体，一般不为空，为空的话表示特定的消失 --- 墓碑消失
     */
    private final V value;   //值
    /**
     * 这个时间戳有两种类型，CreateTime（创建时间） LogAppendTime(消息追加到日志文件的时间)
     */
    private final Long timestamp;   //时间戳，是每一条消息的
```  
- kafka的时间戳  
- 
| 类型              | 含义          | 说明                    |
| --------------- | ----------- | --------------------- |
| `CreateTime`    | 消息创建时间      | Producer 发送时指定的时间（默认） |
| `LogAppendTime` | Broker 写入时间 | Kafka Broker 接收消息时的时间 |  
✅ 查询某个时间点之后的 offset：
Kafka 支持按时间戳查询 offset（而不是按 topic 或分区时间戳统一判断）  
🔄 时间戳与幂等性、顺序消费无强绑定  
Kafka 的幂等性和顺序性依赖分区机制与发送顺序，而不是时间戳。因此：  
时间戳可用于追踪，但不影响消费顺序。  
消费端仍然按 offset 顺序消费，不会因为时间戳大小不同而打乱顺序。  
## ConsumerRecord 这个是消费的时候的类型，他包括了一条消息的全部内容  
```java
    private final String topic; 
    private final int partition; 
    private final long offset;  //偏移量，一个区是唯一的
    private final long timestamp;  //
    private final TimestampType timestampType;
    private final int serializedKeySize;  //key大小
    private final int serializedValueSize;  //value大小
    private final Headers headers;  //消息头部
    private final K key;  
    private final V value;
    private final Optional<Integer> leaderEpoch;
```  
| 字段                    | 含义                                                      |
| --------------------- | ------------------------------------------------------- |
| `**topic**`           | 当前消息属于哪个 topic                                          |
| `**partition**`       | 消息所在的分区编号                                               |
| `**offset**`          | 该消息在分区内的偏移量（唯一标识）                                       |
| `**timestamp**`       | 消息的时间戳，类型见 `timestampType`                              |
| `**timestampType**`   | 时间戳类型：`CREATE_TIME`（生产时）或 `LOG_APPEND_TIME`（Broker 存储时） |
| `key`                 | 消息的 key（可以用于分区 hash）                                    |
| `value`               | 消息内容                                                    |
| `headers`             | Kafka headers（类似 HTTP header，可用于附加信息）                   |
| `serializedKeySize`   | key 序列化后的大小（字节）                                         |
| `serializedValueSize` | value 序列化后的大小（字节）                                       |
| `leaderEpoch`         | 领导者 epoch，可用于处理高可用版本控制（Kafka 2.2+）                      |

## KafkaProducer类，他是线程安全的，有返回值的 ，用来发送消息，发送的就是上面的类