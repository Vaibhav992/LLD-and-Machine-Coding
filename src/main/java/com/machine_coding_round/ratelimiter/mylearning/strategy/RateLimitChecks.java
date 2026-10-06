package com.machine_coding_round.ratelimiter.mylearning.strategy;

import com.machine_coding_round.ratelimiter.mylearning.clock.Clock;
import com.machine_coding_round.ratelimiter.mylearning.exception.InvalidRateLimitConfigException;
import com.machine_coding_round.ratelimiter.mylearning.exception.InvalidUserException;

final class RateLimitChecks {
    private RateLimitChecks() {
    }

    static void user(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidUserException("userId is required");
        }
    }

    static void positive(double value, String name) {
        if (value <= 0) {
            throw new InvalidRateLimitConfigException(name + " must be > 0");
        }
    }

    static void clock(Clock clock) {
        if (clock == null) {
            throw new InvalidRateLimitConfigException("clock is required");
        }
    }
}
