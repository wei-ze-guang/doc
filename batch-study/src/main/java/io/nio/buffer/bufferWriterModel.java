package io.nio.buffer;

import lombok.NonNull;
import org.junit.Test;
import org.jetbrains.annotations.NotNull;
import java.nio.Buffer;
import java.nio.CharBuffer;

/**
 * 演示buffer写模式下  position limit  mark  capacity参数变化
 * | 属性         | 含义       | 初始值 | 写入后是否变化？          |
 * | ---------- | -------- | --- | ----------------- |
 * | `position` | 当前写入位置   | 0   | ✅ 会变              |
 * | `limit`    | 写的上限     | 10  | ❌ 不变              |
 * | `capacity` | 容量总大小    | 10  | ❌ 不变              |
 * | `mark`     | 上次打的书签位置 | -1  | ❌ 除非你主动 `.mark()` |
 */

public class bufferWriterModel {
    //在堆内分配缓冲区
    @Test
    public void testAllocate() {
        CharBuffer allocate = CharBuffer.allocate(16);
//        printBufferInfo(allocate);

        allocate.put('t');
        allocate.put('t');
        allocate.put('t');
        allocate.put("ssss");
        printBufferInfo(allocate);

        allocate.flip();

        printBufferInfo(allocate);

        char c = allocate.get();  //只获取一个字符

        allocate.put('d');


        //allocate.clear();  //恢复初始状态，但是数据没删除，可以复用的
        printBufferInfo(allocate);
    }


    static void printBufferInfo(@NonNull Buffer buffer) {
        StringBuffer sb = new StringBuffer();
//        sb.append("capacity: ").append(buffer.capacity()).append("\n");
        sb.append("limit: ").append(buffer.limit()).append("\n");
        sb.append("position: ").append(buffer.position()).append("\n");
        sb.append("hasRemaining: ").append(buffer.hasRemaining()).append("\n");
        sb.append("remaining: ").append(buffer.remaining()).append("\n");
//        sb.append("hasArray: ").append(buffer.hasArray()).append("\n");
//        sb.append("arrayOffset: ").append(buffer.arrayOffset()).append("\n");
        System.out.println(sb.toString());
    }
}

/**
 *     // Invariants: mark <= position <= limit <= capacity
 *     private int mark = -1;
 *     private int position = 0;
 *     private int limit;  //在写模式下就是你还能写多少个，读模式下你还能读多少个
 *     private int capacity;  buffer里面的
 */
