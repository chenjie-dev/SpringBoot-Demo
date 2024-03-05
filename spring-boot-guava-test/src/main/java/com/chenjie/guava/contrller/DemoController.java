package com.chenjie.guava.contrller;

import com.chenjie.guava.service.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @RateLimit(permitsPerSecond = 10)
    @GetMapping("/demo")
    public String demo() {
        return "限流测试";
    }
}

