package com.chenjie.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;


@SpringBootApplication
public class WebfluxDemoApplication {
    public static void main(String[] args) {
        // 设置时区为东8区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(WebfluxDemoApplication.class, args);
    }
}
