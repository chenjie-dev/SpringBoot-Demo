package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestString {

    private static final String testKey = "testKey";
    private static final String testValue = "testValue";
    private static final String testValue2 = "testValue2222222222222";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testString() {
        redisTemplate.opsForValue().set(testKey, testValue, 1, TimeUnit.MINUTES);

        System.out.println(redisTemplate.opsForValue().get(testKey).toString());

        redisTemplate.opsForValue().set(testKey, testValue2, 1, TimeUnit.MINUTES);

        System.out.println(redisTemplate.opsForValue().get(testKey).toString());

    }
}