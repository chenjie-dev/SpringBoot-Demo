package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestHyperLogLog {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 使用 Redis HyperLogLog
     * <p>
     * 说明：
     * <p>
     * 基数不大，数据量不大就用不上，会有点大材小用浪费空间
     * 有局限性，就是只能统计基数数量，而没办法去知道具体的内容是什么
     * 和bitmap相比，属于两种特定统计情况，简单来说，HyperLogLog 去重比 bitmap 方便很多
     * 一般可以bitmap和hyperloglog配合使用，bitmap标识哪些用户活跃，hyperloglog计数
     * <p>
     * 一般使用场景：
     * <p>
     * 统计注册 IP 数
     * 统计每日访问 IP 数
     * 统计页面实时 UV 数
     * 统计在线用户数
     * 统计用户每天搜索不同词条的个数
     */


    @Test
    public void testHyperLogLog() {
        HyperLogLogOperations operations = redisTemplate.opsForHyperLogLog();

        String key1 = "HyperLogLog-key-1";
        String key2 = "HyperLogLog-key-2";

        long currentTime = System.currentTimeMillis() / 1000;

        String userId1 = String.valueOf(currentTime + 1);
        String userId2 = String.valueOf(currentTime + 2);
        String userId3 = String.valueOf(currentTime + 3);
        String userId4 = String.valueOf(currentTime + 4);
        String userId5 = String.valueOf(currentTime + 5);

        // 使用 add 方法添加元素，对应 PFADD 命令
        operations.add(key1, userId1, userId2, userId3);
        // 使用 size 方法获取元素数量，对应 PFCOUNT 命令
        System.out.println(operations.size(key1));     // 3

        // 继续添加元素
        operations.add(key1, userId1, userId4);
        // 再次获取元素数量
        System.out.println(operations.size(key1));     // 4

        // 添加新的 HyperLogLog
        operations.add(key2, userId4, userId5);
        // 获取新的 HyperLogLog 的元素数量
        System.out.println(operations.size(key2));     // 2

        // 使用 union 方法合并两个 HyperLogLog，对应 PFMERGE 命令
        // 集群下使用有问题
//        operations.union("202310", key1, key2);
//        // 获取合并后的 HyperLogLog 的元素数量
//        System.out.println(operations.size("202310"));  // 5

        operations.delete(key1);
        operations.delete(key2);

    }
}