package com.machine_coding_round.ratelimiter.forInterview;

import com.machine_coding_round.ratelimiter.forInterview.clock.Clock;
import com.machine_coding_round.ratelimiter.forInterview.exception.RateLimiterException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 45-min version: fixed boxes, one counter per user. */
public class FixedWindowRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowMs;
    private final Clock clock;
    private final Map<String, WindowState> users = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int limit, long windowMs, Clock clock) {
        if (limit <= 0 || windowMs <= 0 || clock == null) {
            throw new RateLimiterException("limit, windowMs and clock are required");
        }
        this.limit = limit;
        this.windowMs = windowMs;
        this.clock = clock;
    }

    @Override
    public boolean allow(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new RateLimiterException("userId is required");
        }
        long now = clock.nowMillis();
        WindowState state = users.computeIfAbsent(userId, id -> new WindowState());
        synchronized (state) {
            long windowStart = (now / windowMs) * windowMs;
            if (state.windowStart != windowStart) {
                state.windowStart = windowStart;
                state.count = 0;
            }
            if (state.count >= limit) {
                return false;
            }
            state.count++;
            return true;
        }
    }

    private static final class WindowState {
        private long windowStart = -1;
        private int count;
    }
}
