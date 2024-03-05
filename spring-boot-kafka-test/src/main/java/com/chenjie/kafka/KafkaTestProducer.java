package com.chenjie.kafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFutureCallback;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class KafkaTestProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private static final String testTopic = "chenjie-test-topic";

    @Test
    public void test() {
        while (true) {
            kafkaTemplate.send(testTopic, "test" + System.currentTimeMillis())
                    .addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                                     @Override
                                     public void onSuccess(SendResult<String, String> stringStringSendResult) {
                                         log.info(">>>>> 发送成功 <<<<<");
                                     }

                                     @Override
                                     public void onFailure(Throwable throwable) {
                                         log.info(">>>>> 发送失败 <<<<<");
                                     }
                                 }
                    );
        }
    }
}