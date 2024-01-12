package com.chenjie.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class KafkaListenerService {
    private static final String CONSUMER_ID = "chenjie-test-consummer-id";
    private static final String CONSUMER_GROUP_ID = "chenjie-test-consummer-group-id";
    private static final String testTopic = "chenjie-test-topic";


    static AtomicBoolean atomicBoolean1 = new AtomicBoolean(false);
    static AtomicBoolean atomicBoolean2 = new AtomicBoolean(false);
    static AtomicBoolean atomicBoolean3 = new AtomicBoolean(false);
    static AtomicBoolean atomicBoolean4 = new AtomicBoolean(false);
    static AtomicBoolean atomicBoolean5 = new AtomicBoolean(false);
    static AtomicBoolean atomicBoolean6 = new AtomicBoolean(false);
    static AtomicBoolean atomicBoolean7 = new AtomicBoolean(false);

    @KafkaListener(
            topics = testTopic,
            groupId = CONSUMER_GROUP_ID,
            id = CONSUMER_ID,
            topicPartitions = {
                    @TopicPartition(topic = testTopic, partitions = {"0", "1"})
            }
    )
    public void listenLogs1(ConsumerRecord<Object, String> record, Acknowledgment ack) throws InterruptedException {
        String str = record.value();
        long offset = record.offset();
        String topic = record.topic();
        int partition = record.partition();
        log.info("listenLogs1 >>>> Log received from kafka: {}, offset：{}, topic: {}, partition: {}", str, offset, topic, partition);

        if (partition == 0) {
            log.info("listenLogs1 >>>>> 222222222 >>>>> partition >>>>> 0");
            atomicBoolean1.set(true);
        }
        if (partition == 1) {
            log.info("listenLogs1 >>>>> 222222222 >>>>> partition >>>>> 1");
            atomicBoolean2.set(true);
        }
        // 提交offset
        ack.acknowledge();
        if (atomicBoolean1.get() && atomicBoolean2.get() && atomicBoolean3.get() && atomicBoolean4.get() && atomicBoolean5.get() && atomicBoolean6.get() && atomicBoolean7.get()) {
            log.info("都出现了");
        }
    }

    @KafkaListener(
            topics = testTopic,
            groupId = CONSUMER_GROUP_ID,
            id = CONSUMER_ID + "2",
            topicPartitions = {
                    @TopicPartition(topic = testTopic, partitions = {"0", "1"})
            }
    )
    public void listenLogs2(ConsumerRecord<Object, String> record, Acknowledgment ack) {
        String str = record.value();
        long offset = record.offset();
        String topic = record.topic();
        int partition = record.partition();
        if (partition == 0) {
            log.info("listenLogs2 >>>>> 222222222 >>>>> partition >>>>> 0");
            atomicBoolean3.set(true);

        }
        if (partition == 1) {
            log.info("listenLogs2 >>>>> 222222222 >>>>> partition >>>>> 1");
            atomicBoolean4.set(true);

        }
        log.info("listenLogs2 >>>> Log received from kafka: {}, offset：{}, topic: {}, partition: {}", str, offset, topic, partition);
        // 提交offset
        ack.acknowledge();
        if (atomicBoolean1.get() && atomicBoolean2.get() && atomicBoolean3.get() && atomicBoolean4.get() && atomicBoolean5.get() && atomicBoolean6.get() && atomicBoolean7.get()) {
            log.info("都出现了");
        }
    }

    @KafkaListener(
            topics = testTopic,
            groupId = CONSUMER_GROUP_ID,
            id = CONSUMER_ID + "3",
            topicPartitions = {
                    @TopicPartition(topic = testTopic, partitions = {"0", "1", "2"})
            }
    )
    public void listenLogs3(ConsumerRecord<Object, String> record, Acknowledgment ack) {
        String str = record.value();
        long offset = record.offset();
        String topic = record.topic();
        int partition = record.partition();
        if (partition == 0) {
            log.info("listenLogs3 >>>>> 222222222 >>>>> partition >>>>> 0");
            atomicBoolean5.set(true);

        }
        if (partition == 1) {
            log.info("listenLogs3 >>>>> 222222222 >>>>> partition >>>>> 1");
            atomicBoolean6.set(true);
        }
        if (partition == 2) {
            log.info("listenLogs3 >>>>> 222222222 >>>>> partition >>>>> 2");
            atomicBoolean7.set(true);
        }
        log.info("listenLogs3 >>>> Log received from kafka: {}, offset：{}, topic: {}, partition: {}", str, offset, topic, partition);
        // 提交offset
        ack.acknowledge();
        if (atomicBoolean1.get() && atomicBoolean2.get() && atomicBoolean3.get() && atomicBoolean4.get() && atomicBoolean5.get() && atomicBoolean6.get() && atomicBoolean7.get()) {
            log.info("都出现了");
        }
    }

}
