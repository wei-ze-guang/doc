## 消费者参数  
| 参数名                                                     | 类型        | 对谁生效                  | 说明                       |
|---------------------------------------------------------| --------- |-----------------------|--------------------------|
| `bootstrap.servers`                                     | 客户端 → 服务端 | Kafka 集群              | 消费者连接哪个 Kafka 集群         |
| `group.id`                                              | 客户端 → 协调器 | 消费者组                  | 同组共享 offset 和分区          |
| `key.deserializer` `value.deserializer`                 | 客户端       | 消费者                   | 如何把字节反序列化为 Java 对象       |
| `auto.offset.reset`                                     | 客户端       | 当前消费者组的分区             | 如果没有 offset，从哪里开始消费      |
| `enable.auto.commit`                                    | 客户端       | 当前消费者                 | 是否自动提交 offset            |
| `auto.commit.interval.ms`                               | 客户端       | 当前消费者  ，前提是开启了自动提交才有效 | 定期提交已经拉取到的最新每个分区offset   |
| `fetch.min.bytes` `fetch.max.bytes` `fetch.max.wait.ms` | 客户端 & 服务端 | 拉取请求（消费者 → broker）    | 拉数据触发的条件                 |
| `max.poll.records`                                      | 客户端       | poll() 线程             | 单次 poll 返回最多多少条消息        |
| `session.timeout.ms` `heartbeat.interval.ms`            | 客户端 → 协调器 | 消费者组                  | 判断消费者是否“挂了”              |
| `max.poll.interval.ms`                                  | 客户端 → 协调器 | 当前线程                  | 控制多久必须 poll 一次，否则被踢出消费者组 |   
### Kafka 消费者 poll() 全流程参数作用时间线图（文字版）  
```text
客户端线程 —— 调用 poll(timeout=100) 开始阻塞等待
   │
   │
   ├────► 发送 Fetch 请求 给服务端
   │         └─ 附带 fetch.max.bytes、max.partition.fetch.bytes 等参数
   │
   │
服务端收到请求
   │
   │
   ├──► 判断是否有足够数据满足 fetch.min.bytes：
   │         ├─ 如果足够 → 马上准备返回数据（进入“返回数据流程”）
   │         └─ 不足 → 等待数据积累，进入“等待区”
   │
   ├──► 进入等待期间，同时启动这两个计时器：
   │         ├─ fetch.max.wait.ms（服务端最大等待时间）
   │         └─ poll(timeout)（客户端最大等待时间）
   │
   │
   ├──► 等待期间，如果数据积累满足 fetch.min.bytes，立刻触发返回
   │
   ├──► 或者，只要等到了这两者之一就会“强制”返回：
   │         ├─ 达到 fetch.max.wait.ms（即使数据没达标，也返回）
   │         └─ 客户端 poll(timeout) 超时（poll() 会自动结束阻塞）
   │
   ▼
返回数据（FetchResponse）

客户端接收到数据：
   │
   ├──► 先按 fetch.max.bytes、max.partition.fetch.bytes 截取最大数据量
   │
   ├──► 然后再用 max.poll.records 控制返回给用户最多多少条记录
   │
   └──► 返回 records 给业务逻辑代码
   
假设你有 3 个分区：
如果你设置 fetch.min.bytes = 1000
Kafka 服务端必须等总共 3 个分区加起来的返回数据 ≥ 1000 字节 才响应你这次请求。
如果等了 fetch.max.wait.ms（默认 500ms）还没积满，也会提前返回。

Kafka 是按分区拉取的，一次 poll() 会包含多个分区的数据。
每个分区的数据都在一个统一的 FetchResponse 里。
所以你 poll() 后，可能看到很多条消息，来自不同的分区。
举例来说：你有 3 个分区，Kafka 一次 FetchResponse 就可能包含这 3 个分区的消息，但这仍然是“一次响应”。
```  
* 源码  一次poll会发生多个fetch
```java
    public ConsumerRecords<K, V> poll(Duration timeout) {
        Timer timer = this.time.timer(timeout);
        this.acquireAndEnsureOpen();

        try {
            this.wakeupTrigger.setFetchAction(this.fetchBuffer);
            this.kafkaConsumerMetrics.recordPollStart(timer.currentTimeMs());
            if (this.subscriptions.hasNoSubscriptionOrUserAssignment()) {
                throw new IllegalStateException("Consumer is not subscribed to any topics or assigned any partitions");
            } else {
                this.applicationEventHandler.add(new PollApplicationEvent(timer.currentTimeMs()));

                do {
                    this.wakeupTrigger.maybeTriggerWakeup();
                    this.updateAssignmentMetadataIfNeeded(timer);
                    Fetch<K, V> fetch = this.pollForFetches(timer);
                    if (!fetch.isEmpty()) {
                        if (fetch.records().isEmpty()) {
                            this.log.trace("Returning empty records from `poll()` since the consumer's position has advanced for at least one topic partition");
                        }

                        ConsumerRecords var4 = this.interceptors.onConsume(new ConsumerRecords(fetch.records()));
                        return var4;
                    }
                } while(timer.notExpired());

                ConsumerRecords var8 = ConsumerRecords.empty();
                return var8;
            }
        } finally {
            this.kafkaConsumerMetrics.recordPollEnd(timer.currentTimeMs());
            this.wakeupTrigger.clearTask();
            this.release();
        }
    }
/**
 * 原因分析
 Kafka消费者是多分区消费的
 一个消费者通常会被分配多个分区（partition）。poll() 调用会尝试从所有这些分区拉取数据。
 Fetcher 发起多分区的并发 Fetch 请求
 Fetcher 底层会针对所有分配的分区并发发送 Fetch 请求。它会把每个分区的拉取结果缓存起来。
 poll() 聚合所有分区的消息返回
 当 poll() 收到来自不同分区的 Fetch 返回结果后，会将所有这些分区的消息汇总成一个 ConsumerRecords 对象返回。
 一次 poll() 是一次批量拉取
 poll() 不是针对单个分区拉取，而是从消费者所有分配的分区批量拉取数据，这样能提高效率和吞吐。
 */

```

### 易出错  
小结
客户端时间短不会导致挂起，但会频繁空拉。
一般调优思路是客户端等待时间(就是poll()里面那个参数) >= 服务端等待时间，避免无谓频繁空拉。
## 重点参数解释 + 推荐设置  
#### fetch.min.bytes（服务端推迟发送的下限）
- 含义：消费者最少等到这么多字节后才从服务端拉消息。
- 默认：1（强烈建议保持小）
- 推荐：1~4096，除非你网络带宽非常好又追求吞吐。  ❗误设过大（如 5MB），可能拉不到消息而你以为“Kafka 没数据”。  
#### fetch.max.bytes（客户端可接受的最大响应大小） 默认：50MB（52428800）
- 推荐：按最消息大小设置 + 一些预留，比如 10MB~50MB。
- 跟 max.partition.fetch.bytes 组合使用，后者是单个分区最大量，别让两者差距太大。  
#### max.poll.records 默认：500
```text
📘 官方原文 + 中文翻译
The minimum amount of data the server should return for a fetch request.
➡️ 服务端为每次 fetch 请求返回的最小数据量。
If insufficient data is available the request will wait for that much data to accumulate before answering the request.
➡️ 如果没有达到这个最小数据量，服务端会等待，直到凑够再返回数据。
The default setting of 1 byte means that fetch requests are answered as soon as that many byte(s) of data is available or the fetch request times out waiting for data to arrive.
➡️ 默认值是 1 字节，也就是说，只要服务端有任何数据（≥1字节），就会立刻返回；否则就会等待一段时间直到超时或有数据。
Setting this to a larger value will cause the server to wait for larger amounts of data to accumulate which can improve server throughput a bit at the cost of some additional latency.
➡️ 如果设置为更大的值（比如几 KB），Kafka 会等更多数据积累后才返回，这样能提升吞吐量，但会增加延迟。
```
- 含义：一次 poll 最多返回多少条记录（按条数，不是字节）。
- 推荐：100~1000，根据你每条消息处理耗时调整。  
#### auto.offset.reset 默认值：latest
- 建议显式设置为："earliest"：第一次消费就想拿全历史。"latest"：只想拿新数据。"none"：不建议用，容易异常。
#### enable.auto.commit 默认：true（自动提交）
- 推荐：生产环境改为 false，用 commitSync() 或 commitAsync() 提交，更安全。
#### session.timeout.ms vs heartbeat.interval.ms   默认：session 10s，heartbeat 3s  注意：heartbeat 太低浪费资源，太高丢失消费者不及时  
#### max.poll.interval.ms    默认：5 分钟（300000）
- 意义：最大 poll 间隔，超时被移除。  如果你业务处理慢（比如消费后要写数据库），一定设置大点！   
#### max. partition. fetch. bytes 单个分区拉取的最大字节数  
```text
✅ 总结翻译（人话版）： 这是官网的
max.partition.fetch.bytes 是控制单个分区最多返回多少字节的数据。
Kafka 会尽量遵守这个限制，但如果某条消息批次本身就比这个大，也会强行返回它，不然消费者就卡住了，永远也处理不了。
如果你要限制的是「整个请求总共能拉多少字节」，那要看 fetch.max.bytes。
```  
#### fetch.max.bytes单次拉取所有分区的最大数量  
```text
原文内容简析：
fetch.max.bytes
服务端对一次 Fetch 请求应返回的**最大数据量（字节）**限制。消费者是批量拉取的，拉取的数据是按分区分批的。
如果第一个非空分区的第一批消息大小超过了这个限制，Kafka 也会返回这批消息，保证消费者能继续前进（不会卡住）。
因此，这个参数不是绝对的最大值。
另外，服务端能接受的最大消息批次大小由两个参数控制：
message.max.bytes （Broker 配置）
max.message.bytes （Topic 配置）
消费者是并行执行多个 Fetch 请求的。
该参数类型是 int，默认是 52428800（约 50MB）。
例子说明：
假设设置 fetch.max.bytes=50MB，消费者一次请求服务器最多拉 50MB 的数据（所有分区加起来）。
但是如果某个分区的第一批消息有 60MB，Kafka 会返回这 60MB（超过限制），避免阻塞消费者。

```  
通俗总结：
fetch.max.bytes 控制了一次 Fetch 请求服务端最大返回多少数据（字节），即跨所有分区的总数据限制。 但是如果某个分区里的第一批消息就比这个大，Kafka 会破例返回这批消息，防止消费者被“卡住”。
这个参数不是硬性上限，消息批次的最大大小还有 Broker 和 Topic 的限制。

消费者会同时发出多个并行 Fetch 请求来拉取数据。
---  

- | 参数                          | 常见坑                           |
  | --------------------------- | ----------------------------- |
  | `auto.offset.reset`         | 没设置导致消费者组第一次消费不到历史消息          |
  | `fetch.min.bytes`           | 设置太大，导致 Broker 一直等，拉不到数据      |
  | `enable.auto.commit`        | 默认 true，可能导致**重复消费**或**消息丢失** |
  | `max.poll.interval.ms`      | 消费者处理慢又没设置大一点，被踢出组、Rebalance  |
  | `max.partition.fetch.bytes` | 太小，导致大消息读取失败                  |
  | `group.id` 不唯一              | 多进程部署时组冲突，导致 Rebalance 不断发生   |
