package com.machine_coding_round.ratelimiter.mylearning.clock;

public class SystemClock implements Clock {
    @Override
    public long nowMillis() {
        return System.currentTimeMillis();
    }
}
