package com.chenjie.kafka.aspect;

import com.alibaba.fastjson.JSONObject;
import com.chenjie.kafka.annotation.SendMessage;
import com.chenjie.kafka.service.KafkaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * SendMessage注解切面，向kafka中发送消息
 */
@Aspect
public class SendEmailAspect {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailAspect.class);

    private KafkaService kafkaService;

    public SendEmailAspect(KafkaService<?, ?> kafkaService) {
        this.kafkaService = kafkaService;
    }

    @Pointcut("@annotation(com.chenjie.kafka.annotation.SendMessage)")
    public void pointCutReturn() {

    }

    @AfterReturning(returning = "rvt", pointcut = "pointCutReturn()")
    public void sendEmailSendHandler(JoinPoint joinPoint, Object rvt) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        if (null == signature) {
            logger.error(">>>>>getSignature() is null in joinPoint>>>>>");
            return;
        }

        Method method = signature.getMethod();
        if (method == null) {
            logger.error(">>>>>getMethod() is null in method>>>>>");
            return;
        }

        if (!method.isAnnotationPresent(SendMessage.class)) {
            logger.error(">>>>>SendMessage annotation does not exist in method>>>>>");
            return;
        }

        if (StringUtils.isEmpty(rvt)) {
            logger.error(">>>>>return value is empty in method>>>>>");
            return;
        }

        SendMessage sendMessage = method.getAnnotation(SendMessage.class);
        String topic = sendMessage.value();

        try {
            kafkaService.send(topic, JSONObject.toJSONString(rvt));
            logger.info("send message success topic:[{}], data : [{}]", topic, rvt.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("send message error topic:[{}], data : [{}] , error : [{}]", topic, rvt.toString(), e);
        }

    }
}
