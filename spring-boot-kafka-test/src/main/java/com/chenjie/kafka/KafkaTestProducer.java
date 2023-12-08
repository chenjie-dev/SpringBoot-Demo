package com.chenjie.kafka;

import com.chenjie.kafka.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class KafkaTestProducer {

    @Autowired
    private KafkaService kafkaService;

    private static final String testTopic = "chenjie-test-topic";

    @Test
    public void test() throws InterruptedException, ExecutionException {
        int i = 0;
        while (true) {
            Future result = kafkaService.send(testTopic, "test"+System.currentTimeMillis());
            log.info("result.get() ----> " + result.get());
            log.info("result.isDone() ----> " + result.isDone());
            log.info("睡眠一秒");
            log.info("一共发送{}条消息", ++i);
            Thread.sleep(1000);
        }
    }
}