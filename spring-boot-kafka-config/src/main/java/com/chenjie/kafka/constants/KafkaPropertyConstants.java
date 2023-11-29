package com.chenjie.kafka.constants;

/**
 * kafka配置类
 */
public class KafkaPropertyConstants {
    /**
     * namespace of kafka topic(prefix)
     */
    public static final String SPRING_KAFKA_PRODUCER_NAMESPACE_KEY = "spring.kafka.producer.namespace";
    public static final String SPRING_KAFKA_CONSUMER_NAMESPACE_KEY = "spring.kafka.consumer.namespace";
    /**
     * default namespace(prefix)
     */
    public static final String DEFAULT_SPRING_KAFKA_NAMESPACE = "";

    public static final String SPRING_APPLICATION_NAME_KEY = "spring.application.name";

    public static final String ACTIVE_KAFKA_ENABLED = "spring.aa-kafka.enabled";
    public static final String KAFKA_ENABLED = "spring.kafka.enabled";

    public static final String PUBLIC_NAMESPACE_PREFIX = "public";
    public static final String SPRING_KAFKA_PRODUCER_THREADS_SIZE = "spring.kafka.producer.threads.size";
}
