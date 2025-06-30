package send.mode;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 发后不管，就是普通的发送消息就不管
 */
public class fireAndForgetMode {
    private static String topicNew = "my-topic-1";
    public static void main(String[] args) throws ExecutionException, InterruptedException {

         ProducerSendMode producer= ProducerSendMode.getInstance();
        for (int i = 0; i < 100; i++) {
            // 有返回值的，就是常见的Future接口的实现，调用get方法会堵塞线程
            try{
                Future<RecordMetadata> send = producer.send(String.valueOf(i));
            }catch(RuntimeException e) {
                e.printStackTrace();
            }
        }
//        producer.close();
    }
}
