package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestBitMap {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ONLINE_USERS_KEY = "online_users";

    @Test
    public void testBitMap() {
        long currentTime = System.currentTimeMillis() / 1000;

        long userId1 = currentTime + 1;
        long userId2 = currentTime + 2;
        long userId3 = currentTime + 3;

        // 模拟用户登录和登出事件
        redisTemplate.opsForValue().setBit(ONLINE_USERS_KEY, userId1, true);
        redisTemplate.opsForValue().setBit(ONLINE_USERS_KEY, userId2, true);
        redisTemplate.opsForValue().setBit(ONLINE_USERS_KEY, userId3, true);

        Boolean status = redisTemplate.opsForValue().getBit(ONLINE_USERS_KEY, userId1);
        System.out.println("userId1状态：" + status);


        List<Long> onlineUserCount = redisTemplate.opsForValue().bitField(ONLINE_USERS_KEY,BitFieldSubCommands.create());
        System.out.println("当前在线用户数：" + onlineUserCount); // 应该输出：3

        redisTemplate.opsForValue().setBit(ONLINE_USERS_KEY, userId2, false);
        System.out.println("当前在线用户数：" + onlineUserCount); // 应该输出：2

        redisTemplate.delete(ONLINE_USERS_KEY);
    }
}