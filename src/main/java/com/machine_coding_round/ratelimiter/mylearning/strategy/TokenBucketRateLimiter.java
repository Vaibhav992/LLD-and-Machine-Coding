package com.machine_coding_round.ratelimiter.mylearning.strategy;

import com.machine_coding_round.ratelimiter.mylearning.clock.Clock;
import com.machine_coding_round.ratelimiter.mylearning.model.RateLimitResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter implements RateLimiter {

    private final double capacity;
    private final double refillPerSec;
    private final Clock clock;
    private final Map<String, Bucket> users = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(double capacity, double refillPerSec, Clock clock) {
        RateLimitChecks.positive(capacity, "capacity");
        RateLimitChecks.positive(refillPerSec, "refillPerSec");
        RateLimitChecks.clock(clock);
        this.capacity = capacity;
        this.refillPerSec = refillPerSec;
        this.clock = clock;
    }

    @Override
    public RateLimitResult allow(String userId) {
        RateLimitChecks.user(userId);
        long now = clock.nowMillis();
        Bucket bucket = users.computeIfAbsent(userId, id -> new Bucket(capacity, now));
        synchronized (bucket) {
            refill(bucket, now);
            if (bucket.tokens < 1.0) {
                return RateLimitResult.deny();
            }
            bucket.tokens -= 1.0;
            return RateLimitResult.allow();
        }
    }

    private void refill(Bucket bucket, long now) {
        double elapsedSec = (now - bucket.lastRefillMillis) / 1000.0;
        if (elapsedSec <= 0) {
            return;
        }
        bucket.tokens = Math.min(capacity, bucket.tokens + elapsedSec * refillPerSec);
        bucket.lastRefillMillis = now;
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
