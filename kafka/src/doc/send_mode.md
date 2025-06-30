## 三种发送方式  
### 发后不管，就是调用send方法之后啥也不管了  
- 这种方式吞吐量很好
```java
/**
 * 发后不管，就是普通发送消息就不管
 */
public class fireAndForgetMode {
    private static String topicNew = "my-topic-1";
    public static void main(String[] args) throws ExecutionException, InterruptedException {

         ProducerSendMode producer= ProducerSendMode.getInstance();
        for (int i = 0; i < 100; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
            try{
                Future<ProducerMetadata> send = producer.send(String.valueOf(i));//指定分区之后，key不会计算分区
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
        producer.close();
    }
}
```  
### 异步(async)回调函数发送  Callback回调函数
