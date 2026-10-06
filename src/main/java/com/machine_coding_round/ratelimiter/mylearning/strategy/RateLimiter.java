package com.machine_coding_round.ratelimiter.mylearning.strategy;

import com.machine_coding_round.ratelimiter.mylearning.model.RateLimitResult;

public interface RateLimiter {
    RateLimitResult allow(String userId);
}
