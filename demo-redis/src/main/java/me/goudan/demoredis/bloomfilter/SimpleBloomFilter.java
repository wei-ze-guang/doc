package me.goudan.demoredis.bloomfilter;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class SimpleBloomFilter {
    private final int size;  //数组总长度
    private final BitSet bitSet;  //位数组
    private final int[] seeds;  //hash 次数

    public SimpleBloomFilter(int size, int[] seeds) {
        this.size = size;
        this.bitSet = new BitSet(size);
        this.seeds = seeds;
    }

    // 添加元素
    public void add(String value) {
        for (int seed : seeds) {
            int hash = hash(value, seed);  //
            /**
             * 为什么要取模 % size？
             * 因为哈希函数会产生一个大整数，比如 156346781
             * 位数组只有 size 个长度，所以必须把它缩到 0 ~ size-1 之间
             * 否则会 ArrayIndexOutOfBoundsException
             */
            bitSet.set(Math.abs(hash % size), true);
        }
    }

    // 判断是否存在
    public boolean contains(String value) {
        for (int seed : seeds) {
            int hash = hash(value, seed);
            if (!bitSet.get(Math.abs(hash % size))) {
                return false; // 只要有一个 bit 不是 1，肯定不存在
            }
        }
        return true; // 可能存在（可能误判）
    }

    // 自定义简单 hash 算法
    private int hash(String value, int seed) {
        int result = 0;
        /**
         * 字符串是不可变的，下面的是复制一份，不是原来的byte数组
         */
        for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
            result = result * seed + b;
        }
        return result;
    }

    // 示例
    public static void main(String[] args) {
        int size = 1 << 24; // 2^24 = 16M bits
        int[] seeds = {7, 11, 13, 31, 37, 61}; // 哈希函数种子

        SimpleBloomFilter filter = new SimpleBloomFilter(size, seeds);

        filter.add("dog");
        filter.add("cat");

        System.out.println(filter.contains("dog")); // true
        System.out.println(filter.contains("cat")); // true
        System.out.println(filter.contains("fish")); // false（大概率）
    }
}
