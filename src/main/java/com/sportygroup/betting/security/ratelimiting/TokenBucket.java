package com.sportygroup.betting.security.ratelimiting;

public class TokenBucket {
    private final long capacity;
    private final double refillRatePerSecond;
    private long tokens;
    private long lastRefillTime;

    public TokenBucket(long capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (tokens >= 1) {
            tokens--;
            return true;
        }
        return false;
    }

    public synchronized long getTokens() {
        refill();
        return tokens;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastRefillTime;
        long newTokens = (long) (timeElapsed / 1000.0 * refillRatePerSecond);
        tokens = Math.min(capacity, tokens + newTokens);
        lastRefillTime = now;
    }
}