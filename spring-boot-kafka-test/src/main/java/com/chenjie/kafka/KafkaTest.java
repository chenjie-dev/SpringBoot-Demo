package com.chenjie.kafka;

import com.chenjie.kafka.service.KafkaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaService kafkaService;

    private static final String testTopic = "chenjie-test-topic";
    @Test
    public void test() throws InterruptedException, ExecutionException {
        while (true) {
            Future result = kafkaService.send(testTopic, "test"+System.currentTimeMillis());
            System.out.println("result.get() ----> " + result.get());
            System.out.println("result.isDone() ----> " + result.isDone());
            System.out.println("睡眠一秒");
            System.out.println();
            Thread.sleep(1000);
        }
    }
}