## 消费消息的的时候重复消费或者消息丢失  
## poll()方法
- 设置为0的话，不管拉没拉到数据都直接返回，用来处理延迟
- 设置为Integer.MAX_VALUE,就是一直消费信息  
### 消息丢失，消息丢失情况
1. 消费者拉取数据后还没处理完，Kafka 就自动提交了 offset。结果消费者宕机，重启后从“已提交的 offset”继续消费，跳过了没处理完的消息。  
** 讲人话就是开启了自动提交，但是确实提交了，但是我消息还没处理就提交了，一旦提交成功，但是消费时候出问题了。下次拉取就会从最新的拉取，就会消息丢失 ，提交发生再处理信息之前
```text
poll -> offset=100 ~ 105
还没处理完 -> auto.commit.interval.ms 到期 -> offset=106 被提交
宕机 -> 重启从 106 开始消费，100 ~ 105 丢失

``` 
- 解决方法：
  - 关闭自动提交：enable.auto.commit=false
  - 使用 commitSync() / commitAsync() 在消息处理完之后手动提交 offset  

### 重复消费  
1. 使用commitSync()或者commitAsync()  无参方法手动提交  
使用无参的提交的话他会默认提交最新拉取到的最新的各个分区的最新的offset+1;
出现重复消费问题大概率是你拉取到消息之后，消息你拉取完了，但是在提交的时候出错了，这时候服务端收不到你的提交，服务端的offset位移没有改变  
你下次拉取的时候还是会拉取到旧的offset ,  就会出现重复消费问题  
- 这里还会出现一个问题  手动异步提交的时候时候可能你还没提交出去，又会拉取到一批消息
- commitSync()或者commitAsync()  还提供了有参数的情况，就是按照分区或offset两个参数提交，但是异步 commitAsync() 还提供了多了一个回调，提交完成或者异常时候的回调 
```text
commitAsync(offset1);
commitAsync(offset2);
结果 offset1 比 offset2 后到达 Kafka broker，于是 Kafka 保存了旧的 offset → 数据重复消费。

```  
### 控制或者关闭消费
