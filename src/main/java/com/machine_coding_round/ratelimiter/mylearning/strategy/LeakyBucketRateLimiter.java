package com.machine_coding_round.ratelimiter.mylearning.strategy;

import com.machine_coding_round.ratelimiter.mylearning.clock.Clock;
import com.machine_coding_round.ratelimiter.mylearning.model.RateLimitResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucketRateLimiter implements RateLimiter {

    private final double capacity;
    private final double leakPerSec;
    private final Clock clock;
    private final Map<String, Bucket> users = new ConcurrentHashMap<>();

    public LeakyBucketRateLimiter(double capacity, double leakPerSec, Clock clock) {
        RateLimitChecks.positive(capacity, "capacity");
        RateLimitChecks.positive(leakPerSec, "leakPerSec");
        RateLimitChecks.clock(clock);
        this.capacity = capacity;
        this.leakPerSec = leakPerSec;
        this.clock = clock;
    }

    @Override
    public RateLimitResult allow(String userId) {
        RateLimitChecks.user(userId);
        long now = clock.nowMillis();
        Bucket bucket = users.computeIfAbsent(userId, id -> new Bucket(now));
        synchronized (bucket) {
            leak(bucket, now);
            if (bucket.level + 1.0 > capacity) {
                return RateLimitResult.deny();
            }
            bucket.level += 1.0;
            return RateLimitResult.allow();
        }
    }

    private void leak(Bucket bucket, long now) {
        double elapsedSec = (now - bucket.lastLeakMillis) / 1000.0;
        if (elapsedSec <= 0) {
            return;
        }
        bucket.level = Math.max(0.0, bucket.level - elapsedSec * leakPerSec);
        bucket.lastLeakMillis = now;
    }

    private static final class Bucket {
        private double level;
        private long lastLeakMillis;

        private Bucket(long now) {
            this.level = 0;
            this.lastLeakMillis = now;
        }
    }
}
