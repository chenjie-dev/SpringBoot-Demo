package com.chenjie.caffeine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;


@SpringBootApplication
@ComponentScan(basePackages = {"com.chenjie.*"})
@EnableCaching
public class CaffeineTestApplication {
    public static void main(String[] args) {
        // 设置时区为东8区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(CaffeineTestApplication.class, args);
    }

}
