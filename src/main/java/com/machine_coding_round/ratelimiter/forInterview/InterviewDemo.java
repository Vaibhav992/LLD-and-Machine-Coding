package com.machine_coding_round.ratelimiter.forInterview;

import com.machine_coding_round.ratelimiter.forInterview.clock.FakeClock;

public class InterviewDemo {
    public static void main(String[] args) {
        System.out.println("=== Rate limiter (interview) ===");

        FakeClock clock = new FakeClock(0);
        RateLimiter fixed = new FixedWindowRateLimiter(3, 1000, clock);
        System.out.println("Fixed window:");
        hit(fixed, clock, 100);
        hit(fixed, clock, 300);
        hit(fixed, clock, 900);
        hit(fixed, clock, 950);
        hit(fixed, clock, 1050);

        FakeClock tokenClock = new FakeClock(0);
        RateLimiter token = new TokenBucketRateLimiter(3, 1.0, tokenClock);
        System.out.println("Token bucket:");
        hit(token, tokenClock, 100);
        hit(token, tokenClock, 200);
        hit(token, tokenClock, 300);
        hit(token, tokenClock, 400);
        hit(token, tokenClock, 1400);
    }

    private static void hit(RateLimiter limiter, FakeClock clock, long atMillis) {
        long delta = atMillis - clock.nowMillis();
        if (delta > 0) {
            clock.advanceMillis(delta);
        }
        System.out.printf("  t=%4dms  %s%n", atMillis, limiter.allow("alice") ? "ALLOW" : "DENY");
    }
}
