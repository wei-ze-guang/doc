package io.nio.pipe;

import lombok.NonNull;
import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 演示两个线程通过管道通信
 */
public class PipeDemo {

    @Test
    public void test() throws IOException {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();

        in.connect(out);

        new ReceiveThread(in).start();
        new SendThread(out).start();

        try {
            Thread.sleep(30000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class ReceiveThread extends Thread {
        private PipedInputStream pipedInputStream ;

        public ReceiveThread(@NonNull PipedInputStream pipedInputStream) {
            super();
            if(pipedInputStream == null) throw new NullPointerException();
            this.pipedInputStream = pipedInputStream;
        }
        @Override
        public void run() {
            byte[] buffer = new byte[16];

            try {
                while (true) {
                    int len = pipedInputStream.read(buffer);

                    while (len != -1) {
                        System.out.println("接收信息成功："+new String(buffer, 0, len));
                        len = pipedInputStream.read(buffer);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                if(pipedInputStream != null) {
                    try {
                        pipedInputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    static class SendThread extends Thread {

        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1,2,3});

        ThreadLocalRandom random = ThreadLocalRandom.current();

        private PipedOutputStream pipedOutputStream ;

        public SendThread(@NonNull PipedOutputStream pipedOutputStream) {
            super();
            if(pipedOutputStream == null) throw new NullPointerException();
            this.pipedOutputStream = pipedOutputStream;
        }

        @Override
        public void run() {
            for (;;){
                int i = random.nextInt();
                i = i>>5;
                int i1 = atomicIntegerArray.get(0);
                atomicIntegerArray.getAndIncrement(0);

                try {
                    pipedOutputStream.write(String.valueOf(i1).getBytes());
                    System.out.println("发送信息成功");
                    Thread.sleep(300);
                }catch (InterruptedException e) {
                    boolean interrupted = interrupted();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
