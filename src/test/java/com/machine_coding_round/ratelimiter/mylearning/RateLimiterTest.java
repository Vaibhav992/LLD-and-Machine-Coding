package com.machine_coding_round.ratelimiter.mylearning;

import com.machine_coding_round.ratelimiter.mylearning.clock.FakeClock;
import com.machine_coding_round.ratelimiter.mylearning.exception.InvalidRateLimitConfigException;
import com.machine_coding_round.ratelimiter.mylearning.exception.InvalidUserException;
import com.machine_coding_round.ratelimiter.mylearning.model.RateLimitResult;
import com.machine_coding_round.ratelimiter.mylearning.strategy.FixedWindowRateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.LeakyBucketRateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.RateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.SlidingWindowLogRateLimiter;
import com.machine_coding_round.ratelimiter.mylearning.strategy.TokenBucketRateLimiter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    @Test
    void fixedWindowAllowsUntilLimitThenResets() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new FixedWindowRateLimiter(3, 1000, clock);

        assertAllowed(limiter, clock, 100);
        assertAllowed(limiter, clock, 300);
        assertAllowed(limiter, clock, 900);
        assertDenied(limiter, clock, 950);
        assertAllowed(limiter, clock, 1050);
    }

    @Test
    void fixedWindowKeepsUsersSeparate() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new FixedWindowRateLimiter(1, 1000, clock);

        assertTrue(limiter.allow("alice").isAllowed());
        assertFalse(limiter.allow("alice").isAllowed());
        assertTrue(limiter.allow("bob").isAllowed());
    }

    @Test
    void slidingLogDropsTimestampsOutsideWindow() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new SlidingWindowLogRateLimiter(3, 1000, clock);

        assertAllowed(limiter, clock, 100);
        assertAllowed(limiter, clock, 300);
        assertAllowed(limiter, clock, 600);
        assertDenied(limiter, clock, 800);
        assertAllowed(limiter, clock, 1200);
    }

    @Test
    void tokenBucketAllowsBurstThenRefills() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new TokenBucketRateLimiter(3, 1.0, clock);

        assertAllowed(limiter, clock, 100);
        assertAllowed(limiter, clock, 200);
        assertAllowed(limiter, clock, 300);
        assertDenied(limiter, clock, 400);
        assertAllowed(limiter, clock, 1400);
    }

    @Test
    void leakyBucketRejectsOverflowThenLeaks() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new LeakyBucketRateLimiter(3, 1.0, clock);

        assertAllowed(limiter, clock, 100);
        assertAllowed(limiter, clock, 200);
        assertAllowed(limiter, clock, 300);
        assertDenied(limiter, clock, 400);
        assertAllowed(limiter, clock, 1400);
    }

    @Test
    void blankUserIsRejected() {
        RateLimiter limiter = new FixedWindowRateLimiter(3, 1000, new FakeClock(0));
        assertThrows(InvalidUserException.class, () -> limiter.allow("  "));
        assertThrows(InvalidUserException.class, () -> limiter.allow(null));
    }

    @Test
    void badConfigIsRejected() {
        FakeClock clock = new FakeClock(0);
        assertThrows(InvalidRateLimitConfigException.class, () -> new FixedWindowRateLimiter(0, 1000, clock));
        assertThrows(InvalidRateLimitConfigException.class, () -> new TokenBucketRateLimiter(3, 0, clock));
        assertThrows(InvalidRateLimitConfigException.class, () -> new SlidingWindowLogRateLimiter(3, 1000, null));
        assertThrows(InvalidRateLimitConfigException.class, () -> new LeakyBucketRateLimiter(-1, 1, clock));
    }

    @Test
    void concurrentFixedWindowNeverExceedsLimit() throws InterruptedException {
        RateLimiter limiter = new FixedWindowRateLimiter(50, 60_000, new FakeClock(0));
        int threads = 8;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicInteger allowed = new AtomicInteger();
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                for (int n = 0; n < 20; n++) {
                    if (limiter.allow("alice").isAllowed()) {
                        allowed.incrementAndGet();
                    }
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(50, allowed.get());
    }

    private static void assertAllowed(RateLimiter limiter, FakeClock clock, long atMillis) {
        jump(clock, atMillis);
        RateLimitResult result = limiter.allow("alice");
        assertTrue(result.isAllowed());
        assertEquals("allowed", result.getMessage());
    }

    private static void assertDenied(RateLimiter limiter, FakeClock clock, long atMillis) {
        jump(clock, atMillis);
        RateLimitResult result = limiter.allow("alice");
        assertFalse(result.isAllowed());
        assertEquals("rate limit exceeded", result.getMessage());
    }

    private static void jump(FakeClock clock, long atMillis) {
        clock.advanceMillis(atMillis - clock.nowMillis());
    }
}
