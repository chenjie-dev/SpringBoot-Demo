package com.chenjie.redis.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

/**
 * redis连接异常事件
 */
public class RedisConnectionErrorEvent extends ApplicationEvent {
    private String msg;

    private Throwable e;
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public RedisConnectionErrorEvent(Object source) {
        super(source);
    }

    public RedisConnectionErrorEvent(@NonNull Object source, String msg, Throwable e) {
        super(source);
        this.msg = msg;
        this.e = e;
    }

    public String getMsg() {
        return msg;
    }

    public Throwable getE() {
        return e;
    }
}
