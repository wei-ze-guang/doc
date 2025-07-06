package me.goudan.demoredis.bloomfilter;

import java.util.BitSet;

public class bit {
    public static void main(String[] args) {
        // 创建了一个长度“初始为 10 位”的 位数组（BitSet），可以存放 10 个 bit（位）。
        BitSet bitSet = new BitSet(10);

// 设置第 5 位为 true（即置 1）
        bitSet.set(5);

// 判断第 5 位是否为 true
        System.out.println(bitSet.get(5)); // true

// 清除第 5 位（变成 false，即置 0）
        bitSet.clear(5);

// 输出整个 bitSet 的内容
        System.out.println(bitSet); // {}（空集合，代表全是 0）
    }
}
