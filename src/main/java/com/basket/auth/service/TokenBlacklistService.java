package com.basket.auth.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // jti -> token expiry time (millis)
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String jti, long expiryMillis) {
        blacklist.put(jti, expiryMillis);
        cleanup();
    }

    public boolean isBlacklisted(String jti) {
        return blacklist.containsKey(jti);
    }

    // expired tokens ko hata do, memory na bhare
    private void cleanup() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(e -> e.getValue() < now);
    }
}