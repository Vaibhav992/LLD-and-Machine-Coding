package com.machine_coding_round.ratelimiter.forInterview;

import com.machine_coding_round.ratelimiter.forInterview.clock.FakeClock;
import com.machine_coding_round.ratelimiter.forInterview.exception.RateLimiterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewRateLimiterTest {

    @Test
    void fixedWindowAllowsUntilLimitThenResets() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new FixedWindowRateLimiter(3, 1000, clock);

        assertTrue(hit(limiter, clock, 100));
        assertTrue(hit(limiter, clock, 300));
        assertTrue(hit(limiter, clock, 900));
        assertFalse(hit(limiter, clock, 950));
        assertTrue(hit(limiter, clock, 1050));
    }

    @Test
    void tokenBucketAllowsBurstThenRefills() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new TokenBucketRateLimiter(3, 1.0, clock);

        assertTrue(hit(limiter, clock, 100));
        assertTrue(hit(limiter, clock, 200));
        assertTrue(hit(limiter, clock, 300));
        assertFalse(hit(limiter, clock, 400));
        assertTrue(hit(limiter, clock, 1400));
    }

    @Test
    void usersAreIndependent() {
        FakeClock clock = new FakeClock(0);
        RateLimiter limiter = new FixedWindowRateLimiter(1, 1000, clock);
        assertTrue(limiter.allow("alice"));
        assertFalse(limiter.allow("alice"));
        assertTrue(limiter.allow("bob"));
    }

    @Test
    void invalidInputThrows() {
        FakeClock clock = new FakeClock(0);
        assertThrows(RateLimiterException.class, () -> new FixedWindowRateLimiter(0, 1000, clock));
        assertThrows(RateLimiterException.class, () -> new TokenBucketRateLimiter(3, 1, null));
        RateLimiter limiter = new FixedWindowRateLimiter(1, 1000, clock);
        assertThrows(RateLimiterException.class, () -> limiter.allow(""));
    }

    private static boolean hit(RateLimiter limiter, FakeClock clock, long atMillis) {
        clock.advanceMillis(atMillis - clock.nowMillis());
        return limiter.allow("alice");
    }
}
