package com.chenjie.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestStream {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testStream() {
        // 定义一个 Stream 的 key
        String streamKey = "test-redis-stream-key";
        String streamGroup = "test-redis-stream-group";

        // 创建一个消息
        Map<String, Object> message = new HashMap<>();
        message.put("id", "1");
        message.put("content", "Hello, Redis Stream!");


        String group = redisTemplate.opsForStream().createGroup(streamKey, streamGroup);
        // 发布消息到 Stream
        RecordId recordId = redisTemplate.opsForStream().add(streamKey, message);

        // 读取 Stream 中的消息
        StreamOperations<String, Object, Object> streamOperations = redisTemplate.opsForStream();
        List<MapRecord<String, Object, Object>> messages = streamOperations.read(StreamOffset.fromStart(streamKey));

        // 打印读取到的消息
        for (MapRecord<String, Object, Object> record : messages) {
            System.out.println("Message ID: " + record.getId());
            System.out.println("Message Data: " + record.getValue());
        }

        // 确认消息已消费
        redisTemplate.opsForStream().acknowledge(streamKey, group, recordId);
        redisTemplate.delete(streamKey);
    }
}