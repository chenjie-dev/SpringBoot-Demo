package com.chenjie.elasticsearch;



import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


@SpringBootApplication
@MapperScan(value = "com.chenjie.elasticsearch")
@EnableScheduling
public class ESDemoApplication {
    public static void main(String[] args) {
        // 设置时区为东8区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(ESDemoApplication.class, args);
    }
}
