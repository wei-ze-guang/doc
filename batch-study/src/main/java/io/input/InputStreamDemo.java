package io.input;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class InputStreamDemo {

    @Test
    public void testRead()  {
        InputStream in = null;
        try {
            //从当前目录找的
           in = new FileInputStream("test_io.txt");
           int b;
           while ((b = in.read()) != -1) {
               //java的char是有符号的，只能是-128到127
               System.out.print((char) b);
           }

           //java字节读取的话是八个  00或者11  ，  八字节比无符号表示的是0-255  所以他返回int  使用低8位表示读到的实际字节

       }catch (Exception e) {
            System.out.println(e);
       }finally {
           try {
               if (in != null)   in.close();
           }catch (Exception e) {
               System.out.println(e);
           }
       }

    }

    @Test
    public void testReadLength()  {
        InputStream in = null;
        try {

            //read(b, off, len)：只填充数组的一部分
            //从当前目录找的
            in = new FileInputStream("test_io.txt");
            int length = 5;

            byte[] buffer = new byte[100];

            int len = in.read(buffer, 2, length); // 把 length个字节放到 buffer[2] 开始//但会读取到的实际长度

            in.read(buffer);  //实际调用的就是上面的三个参数的  in.read(buffer, 0，buffer.length); // 把 length个字节放到 buffer[2] 开始//但会读取到的实际长度

            System.out.println("还有多少"+in.available());

            System.out.println(len);

            for(byte c : buffer) System.out.print((char) c);

        }catch (Exception e) {
            System.out.println(e);
        }finally {
            try {
                if (in != null)   in.close();
            }catch (Exception e) {
                System.out.println(e);
            }
        }

    }

    @Test
    public void testOutputStream(){
        OutputStream out = null;
        try {
            String s = """
                    ddsdsdasdddddddddddddddddd 
                    454444444444444444444444444444
                    tttttttttt增长幅度阿德实打实的撒大撒旦撒的撒大撒的撒大sa
                    """;

            String ss = """
                    ssssssssssssssssssddsdsdasdddddddddddddddddd dsaaaaaaaaaaaaaaaaa
                    454444444444444444444444444444dsaddddddddddddddddddddddd
                    tttttttttt增长幅度阿德实打实的撒大撒旦撒的撒大撒的撒大sa
                    """;


            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

            out = new FileOutputStream("out.txt");
            out.write(bytes);

            out.write(55); // 写入 ASCII 'A'

            out.write(ss.getBytes(StandardCharsets.UTF_8),8,ss.length()-8);

            out.flush(); //flush() —— 刷新缓冲区（⚠️ 很关键）

        }catch (Exception e) {
            System.out.println(e);
        }finally {
            try{
                if(out != null) out.close();
            }catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

/**
 * public abstract class InputStream implements Closeable {
 *     public abstract int read() throws IOException;
 *     public int read(byte[] b) throws IOException {...}  // 读入数组
 *     public int read(byte[] b, int off, int len) throws IOException {...}  // 指定偏移读入
 *     public long skip(long n) throws IOException {...} // 跳过 n 个字节
 *     public int available() throws IOException {...}   // 返回还有多少可读
 *     public void close() throws IOException {...}      // 关闭流
 *     public synchronized void mark(int readlimit) {...}
 *     public synchronized void reset() throws IOException {...}
 *     public boolean markSupported() {...}
 * }
 */

/**
 * public abstract class OutputStream implements Closeable, Flushable {
 *     public abstract void write(int b) throws IOException;
 *     public void write(byte[] b) throws IOException {...}
 *     public void write(byte[] b, int off, int len) throws IOException {...}
 *     public void flush() throws IOException {...}
 *     public void close() throws IOException {...}
 * }
 */
