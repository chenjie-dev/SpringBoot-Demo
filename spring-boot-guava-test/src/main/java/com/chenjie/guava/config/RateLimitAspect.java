package com.chenjie.guava.config;

import com.chenjie.guava.entity.TokenBucket;
import com.chenjie.guava.service.RateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private TokenBucket tokenBucket;

    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        if (tokenBucket == null) {
            tokenBucket = new TokenBucket(rateLimit.permitsPerSecond());
        }

        if (tokenBucket.tryAcquire()) {
            return joinPoint.proceed();
        } else {
            throw new RuntimeException("请求被限流");
        }
    }
}
