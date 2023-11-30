package com.chenjie.hystrix;

import com.chenjie.hystrix.service.HystrixService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class HystrixTest {

    @Autowired
    private HystrixService hystrixService;

    @Test
    public void testCircuitBreaker() {

        // 模拟正常情况下的调用
        String result = hystrixService.doSomething(true);
        assertEquals("Success", result);

        // 模拟熔断条件
        result = hystrixService.doSomething(false);

        System.out.println("Circuit breaker");
        System.out.println(result);

        assertEquals("Fallback", result);
    }

}