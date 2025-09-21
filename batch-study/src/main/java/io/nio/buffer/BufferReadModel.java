package io.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Random;

public class BufferReadModel {

    private static Random rand = new Random();

    @Test
    public void test() {
        //一个一个读取
        int capacity = 128;
        CharBuffer allocate = CharBuffer.allocate(capacity);
        for (int i = 0; i < capacity; i++) {

            int i1 = rand.nextInt(capacity);  //0 到 capacity - 1

            char c = (char) (i1 % ((int)Character.MAX_VALUE) );

            allocate.put(c);

        }

        System.out.println(Arrays.toString(allocate.array()));

        bufferWriterModel.printBufferInfo(allocate);

        //转换一下模式读取
        CharBuffer flip = allocate.flip();  //他是会返回原来的buffer的

        char[] str = new char[allocate.capacity()];

        int index = 0;
        while (flip.hasRemaining()) {
            char c = allocate.get();  //一个一个读取
            str[index++] = c;
        }

        String s = new String(str);

        System.out.println(s);

    }

    @Test
    public void test2() {
        //批量读取
        int capacity = 128;
        CharBuffer allocate = CharBuffer.allocate(capacity);
        for (int i = 0; i < capacity; i++) {

            int i1 = rand.nextInt(capacity);  //0 到 capacity - 1

            char c = (char) (i1 % ((int)Character.MAX_VALUE) );

            allocate.put(c);

        }
        allocate.flip();  //记得切换模式
        char[] result = new char[8];

        while (allocate.hasRemaining()) {
            int min = Math.min(result.length, allocate.remaining());  //取小的哪个
            allocate.get(result, 0, min);
            System.out.println(Arrays.toString(result));
        }
    }

    @Test
    public void test3() {
        //缓冲区的复试
        //批量读取
        int capacity = 128;
        CharBuffer allocate = CharBuffer.allocate(capacity);
        for (int i = 0; i < capacity; i++) {

            int i1 = rand.nextInt(capacity);  //0 到 capacity - 1

            char c = (char) (i1 % ((int)Character.MAX_VALUE) );

            allocate.put(c);

        }
        CharBuffer duplicate = allocate.duplicate();

        /**
         * CharBuffer.duplicate() 复制的是“视图”（共享数据，独立指针）他们用到的数组是一样的，但是就是就是那几个参数是独立的
         */
        System.out.println(allocate.hashCode() == duplicate.hashCode());  //这是两个对象但是他重写了hashCode，使用的是数组
    }

    /**
     * 第二种创建buffer的方式
     */
    @Test
    public void test4() {
        int capacity = 128;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);  //对外创建方式只能是byteBuffer

        LongBuffer allocate = LongBuffer.allocate(10000);
        for (int i = 0; i < capacity; i++) {
            int i1 = rand.nextInt(capacity);
            allocate.put(i);
        }
        allocate.flip();
        while (allocate.hasRemaining()) {
            long l = allocate.get();
            System.out.println(l);
        }

    }
    /**
     * | 类型            | 是否支持直接缓冲区   | 如何获取                                    |
     * | ------------- | ----------- | --------------------------------------- |
     * | `ByteBuffer`  | ✅           | `ByteBuffer.allocateDirect()`           |
     * | `CharBuffer`  | ❌（自己不能直接创建） | 可以通过 `ByteBuffer` 的 `asCharBuffer()` 转换 |
     * | `IntBuffer`   | ❌           | 同样通过 `ByteBuffer` 转换                    |
     * | `FloatBuffer` | ❌           | 同上                                      |
     * | `LongBuffer`  | ❌           | 同上                                      |
     */

    @Test
    /**
     * 第三种创建方式
     */
    public void test5() {
        int capacity = 128;
        byte[] bytes = new byte[capacity];
        ByteBuffer.wrap(bytes);  //这样子在外面套作bytes，也会直接改变缓冲区的值
    }
}
