package com.chenjie.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.TimeZone;


@SpringBootApplication
@ComponentScan(basePackages = {"com.chenjie.*"})
@EnableWebMvc
public class KafkaTestApplication {
    public static void main(String[] args) {
        // 设置时区为东8区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(KafkaTestApplication.class, args);
    }

}
