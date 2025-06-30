## 三种发送方式 
## 先将close方法，这个方法很重要  ，close() 是会堵塞线程的
```java
    public static void main(String[] args) {
        /**
         * 回调函数有多种，现在的先写好是一种
         * 2 可以使用匿名内部类，还可以使用
         * 3  Lambda
         * 匿名类和 Lambda 是最常用的写法。
         */
        Callback callback = (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.println("这是回调函数的信息:发送成功，分区：" + metadata.partition());
            }
        };

        ProducerLazy producer = ProducerLazy.getInstance();

        for(int i = 0; i<200;i++){
            try {
                producer.send(String.valueOf(i), callback);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    /**
     * 如果这里不调用 close()  会怎么样子呢，结果是一个输出都没有，close()会等到所有信息发送之后才会关闭生产者
     */
    producer.close();
    }
```
调用 close() 时的阻塞流程  
close() 会阻塞当前线程，直到上述所有的消息都经过完整流程，收到了 ACK 或失败反馈。  
只有确认所有消息处理完毕，close() 才返回，程序继续执行。
上面的如果不调用close方法，由于kafka的send()是异步发送的，但是他好像又不是什么多线程，一不调用close()的话整个main线程结束了，哪来的输出啊，close()不是简单的关闭连接那么简单
你不调用close()也可以，使用flush() flush 一旦开启，不管你这个producerBatch有没有装满，也不会处理哪个linger.ms ，他会立马打包这一批发送  
## close(TimeOut) 带时间的
- close(TimeOut) 就是等你这么多时间，你能发多少发多少,如果计算差距大，会有大量的数据丢失
```java
    static void testCloseTimeOut(){
        /**
         * 测试关闭的时间看一下
         */
        HungryProducer hungryProducer = HungryProducer.getInstance();
        KafkaProducer<String, String> kafkaProducer = hungryProducer.getKafkaProducer();

        List<ProducerRecord<String, String>> records = new LinkedList<ProducerRecord<String,String>>();

        for (int i = 0; i < 1000; i++) {
            /**
             * 此时分区0的数量为1926发送了1000信息
             * 
             * kafkaProducer.close(Duration.ofMillis(10L));
             * 使用十毫米发送的话结果是2450 结果本应该是2926，的明显数量少了，消息没有全部发送完就关闭了kafkaProducer
             */
            ProducerRecord<String, String> stringStringProducerRecord = new ProducerRecord<>(KafkaProducerExample.topicNew, 0,null, "value" + i);
            records.add(stringStringProducerRecord);
        }
        records.forEach(i -> {
            try{
                kafkaProducer.send(i);
            }catch(Exception e){
                e.printStackTrace();
            }

        });

        kafkaProducer.close(Duration.ofMillis(10L));

    }
```
## flush方法,这个方法会堵塞线程额
flush() 方法会强制将之前积压的所有消息发送出去，不管之前是否有发送完成的消息，flush() 之后还可以继续发送信息  
也不管缓冲区中消息是否已经完成发送。换句话说，flush() 是强制刷新缓存，把积压在 Producer 中的所有消息立即发送到 Kafka Broker，并阻塞直到确认完成。
```java
    static void testFlush(){
        HungryProducer hungryProducer = HungryProducer.getInstance();
        KafkaProducer<String, String> kafkaProducer = hungryProducer.getKafkaProducer();

        List<ProducerRecord<String, String>> records = new LinkedList<ProducerRecord<String,String>>();

        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> stringStringProducerRecord = new ProducerRecord<>(KafkaProducerExample.topicNew, null, "value" + i);
            records.add(stringStringProducerRecord);
        }
        records.forEach(i -> {
            try{
                kafkaProducer.send(i);
            }catch(Exception e){
                e.printStackTrace();
            }
        
            //      props.put("linger.ms", 5000);  // 批次等待时间  这里设置了一个很长的等到时间，确实flush()会强行发送
        });

        /**
         * 这里也会阻塞线程，但是生产者没关闭，自己手动关闭
         */
        kafkaProducer.flush();

        /**
         * 这里为了安全闭关一下
         */
        kafkaProducer.close();
    }
```
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
- 他会按照分区顺序性返回的
```java
/**
 * 异步发送
 * 异步发送其实和哪个firstAndForget就是发送完啥也不管的差不多，只不过是增加一个回调函数差不错，就是反馈一下发送结果
 * 但是他和同步发送又有区别，同步发送获取发送结果的话需要使用get方法获取，会堵塞线程，异步发送就不会
 * 回调函数会严格按照发送时间被调用的，kafka的顺序性是按照发送时间，不是写入broker时间或者设置的时间戳的
 */
public class AsyncMode {
    public static void main(String[] args) {
        /**
         * 回调函数有多种，现在的先写好是一种
         * 2 可以使用匿名内部类，还可以使用
         * 3  Lambda
         * 匿名类和 Lambda 是最常用的写法。
         */
        Callback callback = (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.println("这是回调函数的信息:发送成功，分区：" + metadata.partition());
            }
        };

        ProducerLazy producer = ProducerLazy.getInstance();

        for(int i = 0; i<200;i++){
            try {
                producer.send(String.valueOf(i), callback);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        producer.close();


    }
}
```  
### 同步发送方
- 发送方式一  
```java
    /**
     * 方法发
     */
    static void test1(){
        InnerSingletonProducer producer = InnerSingletonProducer.getInstance();
        List<Future<RecordMetadata>> futures = new LinkedList<Future<RecordMetadata>>();
        for (int i = 0; i < 100; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
            try{
                try{
                    Future<RecordMetadata> send = producer.send(String.valueOf(i));
                    try {
                        /**
                         * 这样子的话会堵塞线程，会变为什么样子呢，就是这个ProducerBatch 一直只能获取到一条数据发送出去，效率很低
                         * 因为这里在发送第一条的是时候主线成就被堵塞了，有两种方法
                         * 1  使用线程是发送和获取数据
                         */
                        RecordMetadata recordMetadata = send.get();
                        System.out.println(recordMetadata);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                };
            }catch(RuntimeException e) {
                e.printStackTrace();
            }
        }
        producer.close();
    }
```  
- 同步发送二
```java
    static void test2(){
        /**
         * 这种方法获取消息的话会堵塞当前线程，但是这种的话不会堵塞发送的时候，只是获取结果的时候会
         */
        InnerSingletonProducer producer = InnerSingletonProducer.getInstance();

        List<Future<RecordMetadata>> futures = new LinkedList<Future<RecordMetadata>>();
        for (int i = 0; i < 100; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
            try{
                try{
                    Future<RecordMetadata> send = producer.send(String.valueOf(i));
                    futures.add(send);
                }catch (RuntimeException e){
                    e.printStackTrace();
                };

            }catch(RuntimeException e) {
                e.printStackTrace();
            }
        }
    /**
     * 这里会堵塞线程的，内部会调用flush()
     */
    producer.close();
        futures.forEach(i ->{
            try {
                RecordMetadata recordMetadata = i.get();
                System.out.println("同步接收信息:"+recordMetadata.offset());
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
```  
### 第四种发送方式，flush ,这里一下close() 它内部会调用flush方法,可能就旧版u原因，得找一下
