package com.chenjie.redis.aop;


import com.chenjie.redis.annotation.RedisFallback;
import com.chenjie.redis.event.RedisCEListener;
import com.chenjie.redis.event.RedisCEPublisher;
import com.chenjie.redis.exception.RedisUnhealthyException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * redis降级注解切面
 */
@Aspect
public class RedisFallbackAspect extends AbstractRedisFallbackAspectSupport {
    private static final Logger log = LoggerFactory.getLogger(RedisFallbackAspect.class);
    private final RedisCEPublisher publisher;

    public RedisFallbackAspect(RedisCEPublisher publisher) {
        this.publisher = publisher;
    }

    @Pointcut("@annotation(com.chenjie.redis.annotation.RedisFallback)")
    public void redisFallbackAnnotationPointcut() {
    }

    @Around("redisFallbackAnnotationPointcut()")
    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
        Method method = resolveMethod(pjp);
        RedisFallback annotation = method.getAnnotation(RedisFallback.class);
        boolean cacheAvailable = RedisCEListener.isHealthy.get();
        if (!cacheAvailable) {
            return handleFallback(pjp, annotation, new RedisUnhealthyException("redis cluster state is unhealthy for now."));
        }
        if (annotation == null) {
            throw new IllegalStateException("Wrong state for RedisFallback annotation");
        }
        try {
            return pjp.proceed();
        } catch (Throwable ex) {
            Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
            // The ignore list will be checked first.
            if (exceptionsToIgnore.length > 0 && exceptionBelongsTo(ex, exceptionsToIgnore)) {
                log.error("[{}] error occurs operating redis command", method, ex);
                return null;
            }
            publisher.publish(method, null, ex);
            return handleFallback(pjp, annotation, ex);
        }
    }
}
