package io.input;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;


public class InputStreamDemo {

    @Test
    public void testRead()  {
        InputStream in = null;
        try {
            //从当前目录找的
           in = new FileInputStream("test_io.txt");
           int b;
           while ((b = in.read()) != -1) {
               System.out.print((char) b);
           }


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
