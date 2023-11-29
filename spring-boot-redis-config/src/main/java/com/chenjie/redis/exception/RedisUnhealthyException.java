package com.chenjie.redis.exception;

/**
 * redis健康状态异常类
 */
public class RedisUnhealthyException extends RuntimeException {
    public RedisUnhealthyException() {
    }

    public RedisUnhealthyException(String message) {
        super(message);
    }

    public RedisUnhealthyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisUnhealthyException(Throwable cause) {
        super(cause);
    }

    public RedisUnhealthyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
