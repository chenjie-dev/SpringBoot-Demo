package com.chenjie.redis.constant;

/**
 * @Description redis-starter常量类
 */
public class RedisStarterConstant {

    /**
     * 公共空间前缀
     */
    public static final String publicNamespace = "public";
    /**
     * redis健康状态检查，延迟首次执行的时间
     */
    public static final String DETECT_DELAY = "spring.redis.detector.delay";
    /**
     * redis健康状态检查，检查周期
     */
    public static final String DETECT_PERIOD = "spring.redis.detector.period";
}
