package com.chenjie.redis.event;


import com.chenjie.redis.config.RedisServiceAutoConfigure;
import com.chenjie.redis.aop.RedisFallbackAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * redis事件收发
 */
@Configuration
@ConditionalOnClass(RedisServiceAutoConfigure.class)
@ConditionalOnProperty(value = "spring.redis.event.enabled",havingValue = "true",matchIfMissing = true)
public class RedisEventConfigure {

    @Autowired
    private Environment env;

    @Bean("redisEventListener")
    public RedisCEListener redisCEListener(RedisConnectionFactory redisConnectionFactory, Environment env){
        return new RedisCEListener((LettuceConnectionFactory) redisConnectionFactory,env);
    }

    @Bean
    public RedisCEPublisher redisCEPublisher(ApplicationEventPublisher publisher){
        return new RedisCEPublisher(publisher);
    }

    @Bean
    public RedisFallbackAspect redisFallbackAspect(ApplicationEventPublisher publisher){
        return new RedisFallbackAspect(redisCEPublisher(publisher));
    }
}
