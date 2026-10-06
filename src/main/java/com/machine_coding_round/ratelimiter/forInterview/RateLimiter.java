package com.machine_coding_round.ratelimiter.forInterview;

public interface RateLimiter {
    boolean allow(String userId);
}
