package com.chenjie.hystrix.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

@Service
public class HystrixService {

    @HystrixCommand(fallbackMethod = "fallback")
    public String doSomething(Integer num) {
        if (num == 1) {
            return "1";
        } else if (num == 2) {
            return "2";
        }else {
            throw new RuntimeException("Error occurred");
        }
    }

    public String fallback(Integer num) {
        return "降级返回";
    }
}
