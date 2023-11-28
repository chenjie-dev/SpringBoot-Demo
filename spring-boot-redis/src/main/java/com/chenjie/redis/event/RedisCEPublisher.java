package com.chenjie.redis.event;

import org.springframework.context.ApplicationEventPublisher;

/**
 * redis事件推送器
 */
public class RedisCEPublisher {
    private ApplicationEventPublisher publisher;

    public RedisCEPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(Object source, String message, Throwable e){
        publisher.publishEvent(new RedisConnectionErrorEvent(source,message,e));
    }
}
