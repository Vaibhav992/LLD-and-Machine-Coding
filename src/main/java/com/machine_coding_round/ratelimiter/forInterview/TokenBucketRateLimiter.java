package com.machine_coding_round.ratelimiter.forInterview;

import com.machine_coding_round.ratelimiter.forInterview.clock.Clock;
import com.machine_coding_round.ratelimiter.forInterview.exception.RateLimiterException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 45-min version: burst up to capacity, then refill at a steady rate. */
public class TokenBucketRateLimiter implements RateLimiter {

    private final double capacity;
    private final double refillPerSec;
    private final Clock clock;
    private final Map<String, Bucket> users = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(double capacity, double refillPerSec, Clock clock) {
        if (capacity <= 0 || refillPerSec <= 0 || clock == null) {
            throw new RateLimiterException("capacity, refillPerSec and clock are required");
        }
        this.capacity = capacity;
        this.refillPerSec = refillPerSec;
        this.clock = clock;
    }

    @Override
    public boolean allow(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new RateLimiterException("userId is required");
        }
        long now = clock.nowMillis();
        Bucket bucket = users.computeIfAbsent(userId, id -> new Bucket(capacity, now));
        synchronized (bucket) {
            double elapsedSec = (now - bucket.lastRefillMillis) / 1000.0;
            if (elapsedSec > 0) {
                bucket.tokens = Math.min(capacity, bucket.tokens + elapsedSec * refillPerSec);
                bucket.lastRefillMillis = now;
            }
            if (bucket.tokens < 1.0) {
                return false;
            }
            bucket.tokens -= 1.0;
            return true;
        }
    }

    private static final class Bucket {
        private double tokens;
        private long lastRefillMillis;

        private Bucket(double tokens, long now) {
            this.tokens = tokens;
            this.lastRefillMillis = now;
        }
    }
}
