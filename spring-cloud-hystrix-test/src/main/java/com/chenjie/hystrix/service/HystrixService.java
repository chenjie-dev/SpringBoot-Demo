package com.chenjie.hystrix.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

@Service
public class HystrixService {

    @HystrixCommand(fallbackMethod = "fallback")
    public String doSomething(boolean success) {
        if (success) {
            return "Success";
        } else {
            throw new RuntimeException("Error occurred");
        }
    }

    public String fallback(boolean success) {
        return "Fallback";
    }
}
