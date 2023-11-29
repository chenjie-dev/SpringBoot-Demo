package com.chenjie.cloud.strategy;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * 设置 Hystrix RequestContext 并行策略
 */
public class HystrixRequestContextConcurrencyStrategy extends HystrixConcurrencyStrategy {

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        DelegatingRequestContextCallable<T> wrapperCallable = new DelegatingRequestContextCallable<>(callable,
                requestAttributes);
        Callable<T> callable1 = super.wrapCallable(wrapperCallable);
        return callable1;
    }
}
