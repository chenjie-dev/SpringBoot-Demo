package com.chenjie.guava.entity;

import com.google.common.util.concurrent.RateLimiter;

public class TokenBucket {

    private RateLimiter rateLimiter;

    public TokenBucket(double permitsPerSecond) {
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}