package io.nio.channel;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 演示ServerScoketChannel
 */
public class ServerSocketChannelDemo {

    static boolean isBlocking = false;  //是否堵塞

    public static void main(String[] args) {
        int port = 8080;
        ServerSocketChannel serverSocketChannel = null;
        try {
           serverSocketChannel = ServerSocketChannel.open();
           serverSocketChannel.bind(new InetSocketAddress(port));
           serverSocketChannel.configureBlocking(isBlocking);
        } catch (IOException e) {
            print("打卡ServerSocketChannel失败");
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                //可以设置一个select监听 ，来一个连接就处理连接，每个连接都会返回一个SocketChannel
                SocketChannel accept = serverSocketChannel.accept();  //如果你使用TcpClient连接的话，因为客户端是个，他会发送很多次信息，
                //所以这是只会有一个结果，需要使用一个线程维护这个accept，一直跟他沟通
                //之后呢再设置一个Selector维护这些ScoketChannel  完美
                if (accept == null) {
                    Thread.sleep(100);
                }
                else {

                    //  有东西了，分配内存
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                    byteBuffer.put("hello".getBytes());
                    //先返回结果再说
                    byteBuffer.flip();
                    accept.write(byteBuffer);
                    //然后取出

                    byteBuffer.clear();
                    accept.read(byteBuffer);

                    byteBuffer.flip();  //读模式 ，然后再转码
                    CharBuffer decode = Charset.defaultCharset().decode(byteBuffer);
                    print("收到内容"+decode);
                    accept.close();
                    serverSocketChannel.close();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



    }



    static void print(String msg) {
        System.out.println(msg);
    }
}
