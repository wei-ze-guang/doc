package io.input;

/**
 * 适配器模式的
 *            ┌────────────────────┐
 *            │ 字符流（Reader / Writer）│ ← 操作字符（char）
 *            └────────────────────┘
 *                      ↑
 *       ┌─────────────────────────────┐
 *       │ InputStreamReader / OutputStreamWriter │ ← 编码/解码桥梁
 *       └─────────────────────────────┘
 *                      ↑
 *           ┌──────────────────────┐
 *           │ 字节流（InputStream / OutputStream）│ ← 操作字节（byte）
 *           └──────────────────────┘
 */

import org.junit.Test;

import java.io.*;

/**
 * 🔄 五、字符流和字节流的桥梁：InputStreamReader & OutputStreamWriter
 */
public class ReaderDemo {

    @Test
    public void testReader() {
        InputStream inputStream = null;
        Reader reader = null;

        try {
                inputStream = new FileInputStream("out.txt");

                reader = new InputStreamReader(inputStream);  //适配器

                int ch;

            while ((ch = reader.read()) != -1) {
                System.out.print((char) ch);
            }



        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) inputStream.close();
                if (reader != null) reader.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testWriter() {
        InputStream inputStream = null;
        Reader reader = null;

        OutputStream outputStream = null;

        Writer writer = null;

        try {
            inputStream = new FileInputStream("out.txt");

            reader = new InputStreamReader(inputStream);  //适配器

            writer = new OutputStreamWriter(new FileOutputStream("out_t.txt"));

            int ch;

            while ((ch = reader.read()) != -1) {
                System.out.print((char) ch);
                writer.write(ch);
            }

            writer.flush();  //刷盘

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) inputStream.close();
                if (reader != null) reader.close();
                if (outputStream != null) outputStream.close();
                if (writer != null) writer.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testFileReader() {
        Writer writer = null;
        Reader reader = null;

        try {
            reader = new FileReader("out.txt");
            writer = new FileWriter("out_write.txt");
            int ch;
            while (( (ch = reader.read())!= -1)){
                System.out.print((char) ch);
                writer.write(ch);
            }

            writer.flush(); //刷盘

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null) writer.close();
                if(reader != null) reader.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * int read()            // 读取一个字符（返回 int，-1 表示读完）
 * int read(char[] buf)  // 读取多个字符，写入数组
 * int read(char[], off, len) // 读取 len 个字符到 buf[offset] 开始
 * void close()
 */
