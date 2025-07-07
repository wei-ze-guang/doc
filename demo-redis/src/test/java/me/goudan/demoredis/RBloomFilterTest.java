package me.goudan.demoredis;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RBloomFilterTest {
    @Autowired
    RedissonClient redissonClient;

    @Test
    void t(){
        // 获取布隆过滤器实例
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("myBloomFilter");

      // 初始化容量（预期插入100000条）和误判率（0.03）
        bloomFilter.tryInit(100000L, 0.03);

// 添加元素
        bloomFilter.add("hello");
        bloomFilter.add("world");

// 判断元素是否存在
        boolean exists = bloomFilter.contains("hello");  // true
        boolean notExists = bloomFilter.contains("world");  // false 或 true(误判)
        System.out.println(exists);
        System.out.println(notExists);

// 删除过滤器
        bloomFilter.delete();

    }
}
