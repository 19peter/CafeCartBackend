package com.peters.cafecart.shared.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitingService {

    // Cache of IP addresses to Buckets. 
    // Max 5,000 unique IPs, expired if no activity for 1 hour.
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .maximumSize(5000)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    public Bucket resolveBucket(String ipAddress) {
        return cache.get(ipAddress, key -> createNewBucket());
    }

    private Bucket createNewBucket() {
        // Define the limit: 50 requests per minute
        Bandwidth limit = Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
