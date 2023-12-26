package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestSortedSet {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testSortedSet() {
        // 创建一个 sorted set 类型的 key
        String key = "sorted_set_key";

        // 添加数据到 sorted set 类型的 key
        redisTemplate.opsForZSet().add(key, "item1", 10);
        redisTemplate.opsForZSet().add(key, "item2", 20);
        redisTemplate.opsForZSet().add(key, "item3", 30);

        // 获取 sorted set 类型的 key 中的数据
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        System.out.println("获取 sorted set 类型的 key 中的数据: " + set);
        for (ZSetOperations.TypedTuple<Object> tuple : set) {
            System.out.println(tuple.getValue());
        }

        // 删除 sorted set 类型的 key 中的数据
        redisTemplate.opsForZSet().remove(key, "item1");

        // 判断 sorted set 类型的 key 中是否存在某个元素
        System.out.println("判断 sorted set 类型的 key 中是否存在某个元素：" + redisTemplate.opsForZSet().score(key, "item2"));

        // 获取 sorted set 类型的 key 中某个元素的排名
        System.out.println("获取 sorted set 类型的 key 中某个元素的排名: " + redisTemplate.opsForZSet().rank(key, "item3"));

        // 获取 sorted set 类型的 key 中某个元素的分数
        System.out.println("获取 sorted set 类型的 key 中某个元素的分数: " + redisTemplate.opsForZSet().score(key, "item3"));

        // 获取 sorted set 类型的 key 中所有元素的排名
        Set<ZSetOperations.TypedTuple<Object>> set2 = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        System.out.println("获取 sorted set 类型的 key 中所有元素的排名: ");
        for (ZSetOperations.TypedTuple<Object> tuple : set2) {
            System.out.println(tuple.getValue() + "--" + tuple.getScore());
        }

        // 获取 sorted set 类型的 key 中所有元素的分数
        Set<ZSetOperations.TypedTuple<Object>> set3 = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, 100);
        System.out.println("获取 sorted set 类型的 key 中所有元素的分数: ");
        for (ZSetOperations.TypedTuple<Object> tuple : set3) {
            System.out.println(tuple.getValue() + "--" + tuple.getScore());
        }

        // 删除 sorted set 类型的 key 中的数据
        redisTemplate.delete(key);
    }
}