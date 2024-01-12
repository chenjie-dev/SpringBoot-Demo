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
        String result = hystrixService.doSomething(1);
        assertEquals("1", result);
        System.out.println(result);

        result = hystrixService.doSomething(2);
        assertEquals("2", result);
        System.out.println(result);

        // 模拟熔断条件
        result = hystrixService.doSomething(999);
        System.out.println(result);

        assertEquals("降级返回", result);
    }

}