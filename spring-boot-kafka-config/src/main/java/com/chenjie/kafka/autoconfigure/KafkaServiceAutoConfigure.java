package com.chenjie.kafka.autoconfigure;


import com.chenjie.kafka.annotation.MyKafkaListenerAnnotationBeanPostProcessor;
import com.chenjie.kafka.aspect.SendEmailAspect;
import com.chenjie.kafka.service.KafkaService;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.kubernetes.KubernetesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({KafkaTemplate.class, KubernetesAutoConfiguration.class, ActiveActiveKafkaConfigure.class})
@EnableConfigurationProperties(KafkaProperties.class)
@Order(Integer.MAX_VALUE - 10)
public class KafkaServiceAutoConfigure {
    @Autowired(required = false)
    @Qualifier("aaKafkaTemplate")
    private KafkaTemplate<?, ?> aaKafkaTemplate;
    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<?, ?> kafkaTemplate;
    @Autowired
    private Environment env;
    @Autowired
    private KubernetesClient kubernetesClient;

    public KafkaServiceAutoConfigure() {
    }

    /**
     * kafka功能实现封装类
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @DependsOn("kafkaTemplate")
//    @ConditionalOnBean(name = "kafkaTemplate")
    public KafkaService<?, ?> kafkaService() {
        return new KafkaService(kafkaTemplate, aaKafkaTemplate, env, kubernetesClient);
    }

    /**
     * kafka消费者bean后置处理器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(KafkaService.class)
    public MyKafkaListenerAnnotationBeanPostProcessor myAnnotationProcessor() {
        return new MyKafkaListenerAnnotationBeanPostProcessor(env, kubernetesClient);
    }

    /**
     * kafka消息发送实例
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SendEmailAspect sendEmailAspect() {
        return new SendEmailAspect(kafkaService());
    }
}
