package com.machine_coding_round.ratelimiter.mylearning;

public interface RateLimiter{
    boolean allow(String userId);
}
