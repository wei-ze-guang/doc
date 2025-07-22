package io.nio.channel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TcpClient {

    public static void main(String[] args) throws Exception {
        scoketChannel();
    }

    public static void bioScoket() throws IOException {
        // 连接服务端
        String serverIp = "127.0.0.1";
        int port = 8080;
        Socket socket = new Socket(serverIp, port);
        System.out.println("连接服务端成功：" + serverIp + ":" + port);

        // 获取输出流
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);

        // 定时任务线程池
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 每 3 秒发送一次消息
        scheduler.scheduleAtFixedRate(() -> {
            String message = "你好，服务端！时间：" + System.currentTimeMillis();
            writer.println(message);  // 发送数据
            System.out.println("已发送：" + message);
        }, 0, 3, TimeUnit.SECONDS);
    }

    public static void scoketChannel() throws IOException {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
        SocketChannel socketChannel = SocketChannel.open(address);
        socketChannel.configureBlocking(false); // 非阻塞模式

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            String message = "你好，服务端！时间：" + System.currentTimeMillis();
            buffer.clear();
            buffer.put(message.getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            try {
                socketChannel.write(buffer);
                System.out.println("已发送：" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

}

