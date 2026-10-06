package com.machine_coding_round.ratelimiter.mylearning;

import com.machine_coding_round.ratelimiter.mylearning.clock.FakeClock;
import com.machine_coding_round.ratelimiter.mylearning.strategy.FixedWindowRateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.LeakyBucketRateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.RateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.SlidingWindowLogRateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.TokenBucketRateLimiter;

public class RateLimiterDemo {
    public static void main(String[] args) {
        System.out.println("=== Rate limiter (learning) ===");
        run("Fixed window", clock -> new FixedWindowRateLimiter(3, 1000, clock), new long[]{100, 300, 900, 950, 1050});
        run("Sliding log", clock -> new SlidingWindowLogRateLimiter(3, 1000, clock), new long[]{100, 300, 600, 800, 1200});
        run("Token bucket", clock -> new TokenBucketRateLimiter(3, 1.0, clock), new long[]{100, 200, 300, 400, 1400});
        run("Leaky bucket", clock -> new LeakyBucketRateLimiter(3, 1.0, clock), new long[]{100, 200, 300, 400, 1400});
    }

    private static void run(String name, LimiterFactory factory, long[] atMillis) {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = factory.create(clock);
        System.out.println("--- " + name + " ---");
        long prev = 0;
        for (long t : atMillis) {
            clock.advanceMillis(t - prev);
            prev = t;
            var result = limiter.allow("alice");
            System.out.printf("  t=%4dms  %s (%s)%n", t, result.isAllowed() ? "ALLOW" : "DENY", result.getMessage());
        }
    }

    @FunctionalInterface
    private interface LimiterFactory {
        RateLimiter create(FakeClock clock);
    }
}
