## JAVA中几个利用CAS规范实现的类  基于UnSafe类实现的  
**在使用CAS的时候，底层一定是需要地址 expect值和newValue ，就是比较这个地址的值是不是和expect的值一样，但是886只有两个参数，是三地址指令，
他把需要比较的地址放在RAW寄存器了，所以java层面必须传地址进去。还有就是expect还可以是一个地址，就是比较地址。**  

**CAS和unsafe类都是一次尝试，但是下面的类是通过自旋，无限循环的方式实现，就是一直使用CAS知道成功，是无锁的**  

下面的API对于CPU资源是很浪费的，因为自旋，还会出现ABA问题

| 类名                | 常用方法                                          | 优点                                       | 缺点 / 可能出现的问题                         |
|---------------------|-------------------------------------------------|--------------------------------------------|--------------------------------------------|
| AtomicInteger       | `get()`, `set(int)`, `compareAndSet(int, int)`, `incrementAndGet()`, `decrementAndGet()`, `addAndGet(int)` | 高效的无锁原子操作，适合高并发计数和状态更新  | ABA问题（值变化后又变回原值，CAS误判成功），只能操作单个变量 |
| AtomicLong          | `get()`, `set(long)`, `compareAndSet(long, long)`, `incrementAndGet()`, `decrementAndGet()`, `addAndGet(long)` | 同AtomicInteger，支持长整型               | 同AtomicInteger                              |
| AtomicBoolean       | `get()`, `set(boolean)`, `compareAndSet(boolean, boolean)` | 简单状态开关原子操作                      | 同AtomicInteger                              |
| AtomicReference<V>  | `get()`, `set(V)`, `compareAndSet(V, V)`, `getAndSet(V)` | 支持对象引用的无锁原子更新                 | ABA问题，同步复杂对象时可能不够用，无法保证复合操作原子性   |
| AtomicIntegerArray  | `get(int)`, `set(int, int)`, `compareAndSet(int, int, int)`, `incrementAndGet(int)` | 支持数组元素的原子操作                    | 数组长度固定，操作数组元素时需要小心边界和一致性          |
| AtomicLongArray     | `get(int)`, `set(int, long)`, `compareAndSet(int, long, long)`, `incrementAndGet(int)` | 同AtomicIntegerArray，支持长整型数组      | 同AtomicIntegerArray                          |
| AtomicReferenceArray<E> | `get(int)`, `set(int, E)`, `compareAndSet(int, E, E)`, `getAndSet(int, E)` | 支持对象引用数组的无锁原子操作             | 同AtomicReference，且数组元素操作复杂                |

| CAS类                    | 底层实现细节                                                  |
| ----------------------- | ------------------------------------------------------- |
| AtomicInteger           | 使用 `sun.misc.Unsafe` 的 `compareAndSwapInt` 方法实现CAS操作    |
| AtomicLong              | 使用 `sun.misc.Unsafe` 的 `compareAndSwapLong` 方法实现CAS操作   |
| AtomicBoolean           | 其实是用 `AtomicInteger` 来实现，调用 `compareAndSwapInt`         |
| AtomicReference<V>      | 使用 `sun.misc.Unsafe` 的 `compareAndSwapObject` 方法实现CAS操作 |
| AtomicIntegerArray      | 通过对数组元素偏移量计算后调用 `Unsafe.compareAndSwapInt`              |
| AtomicLongArray         | 通过对数组元素偏移量计算后调用 `Unsafe.compareAndSwapLong`             |
| AtomicReferenceArray<E> | 通过对数组元素偏移量计算后调用 `Unsafe.compareAndSwapObject`           |


## 什么是CAS  
1. 全称是compare and swap 对比和交换，原子性操作，UnSafe类(非标准API) 就是先一读一写，如果和expect的值一样，就交换  
如果和expect的值不一样就返回，不自旋，java中上面的这些原子类就是利用这个和自旋实现的,UnSafe类使用了汇编和X86的指令集

## 使用案例
```java
package casimpl;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Cas {

    private final static ReentrantLock lock = new ReentrantLock();

    private static AtomicInteger counter = new AtomicInteger();

    private static int times = 100000; // 每个线程执行的循环次数

    private static   int count = 0;

    private static int threadNums = 30;  //数量


    public static void main(String[] args) {
        for (int i = 0; i < threadNums; i++) {
            new One().start();
        }

        try {
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            counter.compareAndSet(0,count);  //这里是只有一次，不是自旋
            System.out.println("使用了CAS相关的准群率:"+toPercent(counter.get(),times*threadNums));
            System.out.println("count:"+count);
//            System.out.println("没使用CAS的普通变量:"+toPercent(count,times*threadNums));
            System.out.println("使用synchronized:"+toPercent(count,times*threadNums));
            System.out.println("使用ReentrantLock :"+toPercent(count,times*threadNums));
        }
    }

    static class One extends Thread{
        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
//                counter.incrementAndGet();
//                count++;
//                countAdd();
                lock.lock();
                try {
                    count++;
                }finally {
                    lock.unlock();
                }

            }

        }
    }

    static synchronized void countAdd(){
        count++;
    }

    /**
     * 将两个整数相除，返回百分比字符串，保留两位小数（例如："75.00%"）
     * @param numerator 分子
     * @param denominator 分母
     * @return 格式化的百分比字符串
     */
    public static String toPercent(int numerator, int denominator) {
        if (denominator == 0) {
            return "0.00%"; // 或者你也可以返回 "0.00%"，看具体业务需求
        }
        double ratio = (double) numerator / denominator;
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(ratio);
    }
}

```