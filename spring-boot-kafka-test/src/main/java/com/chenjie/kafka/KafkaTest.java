package com.chenjie.kafka;

import com.alibaba.fastjson.JSONObject;
import com.chenjie.kafka.service.KafkaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaService kafkaService;

    private static final String testTopic = "chenjie-test-topic";
    @Test
    public void test() {
        kafkaService.send(testTopic, "test"+System.currentTimeMillis());
    }
}