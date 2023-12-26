package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestSet {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testSet() {
        // 创建一个 set 类型的 key
        String key = "set_key";

        // 添加数据到 set 类型的 key
        redisTemplate.opsForSet().add(key, "item1");
        redisTemplate.opsForSet().add(key, "item2");
        redisTemplate.opsForSet().add(key, "item3");

        // 获取 set 类型的 key 中的数据
        Set<Object> set = redisTemplate.opsForSet().members(key);
        System.out.println("获取 set 类型的 key 中的数据: " + set);

        // 删除 set 类型的 key 中的数据
        redisTemplate.opsForSet().remove(key, "item1");

        // 判断 set 类型的 key 中是否存在某个元素
        System.out.println("判断 set 类型的 key 中是否存在某个元素：" + redisTemplate.opsForSet().isMember(key, "item2"));

        // 获取 set 类型的 key 中所有元素的数量
        System.out.println("获取 set 类型的 key 中所有元素的数量: " + redisTemplate.opsForSet().size(key));

        // 删除 set 类型的 key 中的数据
        redisTemplate.delete(key);
    }
}