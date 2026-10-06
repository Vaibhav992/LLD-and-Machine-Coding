package com.machine_coding_round.ratelimiter.mylearning.strategy;

import com.machine_coding_round.ratelimiter.mylearning.clock.Clock;
import com.machine_coding_round.ratelimiter.mylearning.model.RateLimitResult;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowLogRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowMs;
    private final Clock clock;
    private final Map<String, Deque<Long>> logs = new ConcurrentHashMap<>();

    public SlidingWindowLogRateLimiter(int limit, long windowMs, Clock clock) {
        RateLimitChecks.positive(limit, "limit");
        RateLimitChecks.positive(windowMs, "windowMs");
        RateLimitChecks.clock(clock);
        this.limit = limit;
        this.windowMs = windowMs;
        this.clock = clock;
    }

    @Override
    public RateLimitResult allow(String userId) {
        RateLimitChecks.user(userId);
        long now = clock.nowMillis();
        Deque<Long> log = logs.computeIfAbsent(userId, id -> new ArrayDeque<>());
        synchronized (log) {
            long cutoff = now - windowMs;
            while (!log.isEmpty() && log.peekFirst() <= cutoff) {
                log.pollFirst();
            }
            if (log.size() >= limit) {
                return RateLimitResult.deny();
            }
            log.addLast(now);
            return RateLimitResult.allow();
        }
    }
}
