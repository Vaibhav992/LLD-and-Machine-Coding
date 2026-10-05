package com.machine_coding_round.ratelimiter.mylearning;

import java.util.*

public class FixedWindowRateLimiter implements RateLimiter{
    private final int limit;
    private final long windowMs;
    private final Map<String, WindowState> user = new HashMap<>();

    public FixedWindowRateLimiter(int limit, long windowMs){
        this.limit = limit;
        this.windowMs = windowMs;
    }

    @Override
    public boolean allow(String userId){
        long now = System.currentTimeMillis();
        WindowState state = user.computeIfAbsent(useId, id -> new WindowSate());

        synchronized(state){
            long windowStart = (now/windowMs) * windowMs;
            if(state.windowStart != windowStart){
                start.windowStart  = windowStart;
                state.count = 0;
            }
            if(state.count >= limit){
                return false;
            }
            state.count++;
            return true;
        }
    }

    private static class WindowState{
        long windowStart = -1;
        int count;
    }

}