package casimpl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * | 方法                                            | 说明                 | 返回值       |
 * | --------------------------------------------- | ------------------ | --------- |
 * | `get(obj)`                                    | 获取变量当前值            | 变量类型      |
 * | `set(obj, value)`                             | 设置变量新值             | `void`    |
 * | `getVolatile(obj)`                            | 按 `volatile` 语义读取  | 变量类型      |
 * | `setVolatile(obj, value)`                     | 按 `volatile` 语义写入  | `void`    |
 * | `getOpaque(obj)`                              | 按最弱内存语义读取          | 变量类型      |
 * | `setOpaque(obj, value)`                       | 按最弱内存语义写入          | `void`    |
 * | `getAcquire(obj)`                             | 获取语义读取（只保证后续不可重排序） | 变量类型      |
 * | `setRelease(obj, value)`                      | 释放语义写入（只保证前面不可重排序） | `void`    |
 * | `compareAndSet(obj, expected, newValue)`      | 原子 CAS             | `boolean` |
 * | `compareAndExchange(obj, expected, newValue)` | 原子交换，返回旧值          | 变量类型      |
 * | `getAndAdd(obj, delta)`                       | 原子加法（适用于数字类型）      | 返回旧值      |
 * | `getAndSet(obj, newValue)`                    | 原子替换值              | 返回旧值      |
 */
public class VarHandlerDemo {
    /**
     * VarHandle 是由 MethodHandles.Lookup 提供的。
     * MethodHandles类似于反射，高级版
     * @param args
     */

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        VarHandleExample varHandleExample = new VarHandleExample();

        // 获取Lookup对象
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        VarHandle handle = lookup.findVarHandle(varHandleExample.getClass(),"b",String.class);

        VarHandle numHandle = lookup.findVarHandle(varHandleExample.getClass(),"a",int.class);

        String value = (String) handle.get(varHandleExample);

        int a = (int) numHandle.get(varHandleExample);

        /**
         * 这个会返回旧值
         */
        numHandle.compareAndExchange(varHandleExample,a,888);

        System.out.println(a);

        /**
         * 这个不会返回旧值
         */
        handle.compareAndSet(varHandleExample,value+"2","newValue");

        String newValue = (String) handle.get(varHandleExample);

        System.out.println("旧值:"+value);

        System.out.println("新值:"+newValue);


        VarHandle vh = null;
    }
}
