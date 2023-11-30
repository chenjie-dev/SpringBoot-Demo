package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    private static final String testKey = "testKey";
    private static final String testValue = "testValue";
    private static final String testValue2 = "testValue2222222222222";
    @Autowired
    private StarterRedisTemplate starterRedisTemplate;

    @Test
    public void test() {
        starterRedisTemplate.opsForValue().set(testKey, testValue, 1, TimeUnit.MINUTES);

        System.out.println(starterRedisTemplate.opsForValue().get(testKey).toString());

        starterRedisTemplate.opsForValue().set(testKey, testValue2, 1, TimeUnit.MINUTES);

        System.out.println(starterRedisTemplate.opsForValue().get(testKey).toString());

    }
}