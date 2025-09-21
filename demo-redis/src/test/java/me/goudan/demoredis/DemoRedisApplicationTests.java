package me.goudan.demoredis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.RedisTemplate;

@DataRedisTest
class DemoRedisApplicationTests {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testRedis(){
            redisTemplate.opsForHash().put("kkkl","hh","tttt");
        Object o = redisTemplate.opsForHash().get("kkkl", "hh");
        System.out.println(o);
    }

}
