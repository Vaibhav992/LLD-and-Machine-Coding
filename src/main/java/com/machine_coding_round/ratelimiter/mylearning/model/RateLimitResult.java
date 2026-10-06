package com.machine_coding_round.ratelimiter.mylearning.model;

import lombok.Getter;

@Getter
public class RateLimitResult {
    private final boolean allowed;
    private final String message;

    private RateLimitResult(boolean allowed, String message) {
        this.allowed = allowed;
        this.message = message;
    }

    public static RateLimitResult allow() {
        return new RateLimitResult(true, "allowed");
    }

    public static RateLimitResult deny() {
        return new RateLimitResult(false, "rate limit exceeded");
    }
}
