package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestList {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testList() {
        // 创建一个 list 类型的 key
        String key = "list_key";

        // 添加数据到 list 类型的 key
        redisTemplate.opsForList().leftPush(key, "item1");
        redisTemplate.opsForList().leftPush(key, "item2");
        redisTemplate.opsForList().leftPush(key, "item3");

        // 获取 list 类型的 key 中的数据
        List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
        System.out.println("获取 list 类型的 key 中的数据: " + list);

        // 删除 list 类型的 key 中的数据
        redisTemplate.opsForList().remove(key, 0, "item1");

        // 获取 list 类型的 key 中某个元素的索引
        System.out.println("获取 list 类型的 key 中某个元素的索引: " + redisTemplate.opsForList().index(key, 1));

        // 获取 list 类型的 key 中所有元素的长度
        System.out.println("获取 list 类型的 key 中所有元素的长度: " + redisTemplate.opsForList().size(key));

        // 删除 list 类型的 key 中的数据
        redisTemplate.delete(key);
    }
}