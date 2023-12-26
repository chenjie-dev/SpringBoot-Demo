package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestHash {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testHash() {
        // 创建一个 hash 类型的 key
        String key = "hash_key";
        String key_field1 = "hash_key_field1";
        String key_field2 = "hash_key_field2";

        String key_value1 = "hash_key_value1";
        String key_value2 = "hash_key_value2";

        // 添加数据到 hash 类型的 key
        redisTemplate.opsForHash().put(key, key_field1, key_value1);
        redisTemplate.opsForHash().put(key, key_field2, key_value2);

        // 获取 hash 类型的 key 中的数据
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        System.out.println("获取 hash 类型的 key 中的数据: " + map);

        // 删除 hash 类型的 key 中的数据
        redisTemplate.opsForHash().delete(key, key_field1);

        // 判断 hash 类型的 key 中是否存在某个 field
        System.out.println("判断 hash 类型的 key 中是否存在某个 field ：" + redisTemplate.opsForHash().hasKey(key, key_field1));

        // 获取 hash 类型的 key 中某个 field 的值
        System.out.println("获取 hash 类型的 key 中某个 field 的值: " + redisTemplate.opsForHash().get(key, key_field2));

        // 获取 hash 类型的 key 中所有 field 的值
        List<Object> values = redisTemplate.opsForHash().values(key);
        System.out.println("获取 hash 类型的 key 中所有 field 的值: " + values);

        // 获取 hash 类型的 key 中所有 field 和 value 的元组
        Set<Object> entries = redisTemplate.opsForHash().entries(key).keySet();
        System.out.println("获取 hash 类型的 key 中所有 field 和 value 的元组：" + entries);

        // 删除 hash 类型的 key 中的数据
        redisTemplate.opsForHash().delete(key, key_field1, key_field2);

    }
}