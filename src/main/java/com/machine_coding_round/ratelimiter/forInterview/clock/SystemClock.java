package com.machine_coding_round.ratelimiter.forInterview.clock;

public class SystemClock implements Clock {
    @Override
    public long nowMillis() {
        return System.currentTimeMillis();
    }
}
