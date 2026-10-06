package com.machine_coding_round.ratelimiter.mylearning.clock;

public class FakeClock implements Clock {
    private long nowMillis;

    public FakeClock(long startMillis) {
        this.nowMillis = startMillis;
    }

    @Override
    public long nowMillis() {
        return nowMillis;
    }

    public void advanceMillis(long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta must be >= 0");
        }
        nowMillis += delta;
    }
}
