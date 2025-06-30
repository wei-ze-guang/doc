package send.mode;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import test.KafkaProducerExample;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class FlushAndClose {
    public static void main(String[] args) {
//        testFlush();
        testCloseTimeOut();
    }
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

        });

        /**
         * 这里也会阻塞线程，但是生产者没关闭，自己手动关闭
         */
        kafkaProducer.flush();
        /**
         * 为了安全关闭一下
         */

    }

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

}
