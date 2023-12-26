package com.chenjie.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaListenerService {
    private static final String CONSUMER_ID = "chenjie-test-consummer-id";
    private static final String CONSUMER_GROUP_ID = "chenjie-test-consummer-group-id";
    private static final String testTopic = "chenjie-test-topic";

    @KafkaListener(topics = testTopic, groupId = CONSUMER_GROUP_ID, id = CONSUMER_ID)
    public void listenLogs(ConsumerRecord<Object, String> record, Acknowledgment ack) {
        String str = record.value();
        long offset = record.offset();
        String topic = record.topic();
        int partition = record.partition();
        log.info("Log received from kafka: {}, offset：{}, topic: {}, partition: {}", str, offset, topic, partition);
        // 提交offset
        ack.acknowledge();
    }

    @KafkaListener(topics = testTopic, groupId = CONSUMER_GROUP_ID, id = CONSUMER_ID + "2")
    public void listenLogs2(ConsumerRecord<Object, String> record, Acknowledgment ack) {
        String str = record.value();
        long offset = record.offset();
        String topic = record.topic();
        int partition = record.partition();
        log.info("listenLogs2 >>>> Log received from kafka: {}, offset：{}, topic: {}, partition: {}", str, offset, topic, partition);
        // 提交offset
        ack.acknowledge();
    }

    @KafkaListener(topics = testTopic, groupId = CONSUMER_GROUP_ID, id = CONSUMER_ID + "3")
    public void listenLogs3(ConsumerRecord<Object, String> record, Acknowledgment ack) {
        String str = record.value();
        long offset = record.offset();
        String topic = record.topic();
        int partition = record.partition();
        log.info("listenLogs3 >>>> Log received from kafka: {}, offset：{}, topic: {}, partition: {}", str, offset, topic, partition);
        // 提交offset
        ack.acknowledge();
    }
}
