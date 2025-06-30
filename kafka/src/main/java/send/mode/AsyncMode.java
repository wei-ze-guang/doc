package send.mode;

import org.apache.kafka.clients.producer.Callback;

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
