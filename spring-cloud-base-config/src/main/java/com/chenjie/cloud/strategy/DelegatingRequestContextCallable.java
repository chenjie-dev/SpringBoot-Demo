package com.chenjie.cloud.strategy;

import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * 全局设置 RequestContext
 * @param <T>
 */
public class DelegatingRequestContextCallable<T> implements Callable<T> {
    private Callable<T> delegate;
    private RequestAttributes requestAttributes;

    public DelegatingRequestContextCallable(Callable<T> delegate, RequestAttributes requestAttributes) {
        Assert.notNull(delegate, "delegate cannot be null");
        this.delegate = delegate;
        this.requestAttributes = requestAttributes;
    }

    @Override
    public T call() throws Exception {
        RequestAttributes originalRequestAttributes = RequestContextHolder.getRequestAttributes();

        try {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            return delegate.call();
        } finally {
            RequestContextHolder.setRequestAttributes(originalRequestAttributes);
            originalRequestAttributes = null;
        }
    }
}
