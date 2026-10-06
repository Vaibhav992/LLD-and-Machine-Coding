package com.machine_coding_round.ratelimiter.forInterview.clock;

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
        nowMillis += delta;
    }
}
