package com.example.urlshortner.service;

import com.example.urlshortner.model.ShortUrl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
public class ShortUrlCache {

    private static final String PREFIX = "shorturl:code:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Duration defaultTtl;

    public record CachedShortUrl(String originalUrl, Instant expiresAt) {
    }

    public ShortUrlCache(
            StringRedisTemplate redis,
            ObjectMapper objectMapper,
            @Value("${app.cache.shorturl.default-ttl-seconds:86400}") long defaultTtlSeconds
    ) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.defaultTtl = Duration.ofSeconds(defaultTtlSeconds);
    }

    public Optional<CachedShortUrl> get(String code) {
        try {
            String raw = redis.opsForValue().get(PREFIX + code);
            if (raw == null) {
                return Optional.empty();
            }
            CachedShortUrl cached = objectMapper.readValue(raw, CachedShortUrl.class);
            if (cached.expiresAt() != null && cached.expiresAt().isBefore(Instant.now())) {
                return Optional.empty();
            }
            return Optional.of(cached);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void put(ShortUrl shortUrl) {
        if (shortUrl.getCode() == null) {
            return;
        }
        try {
            CachedShortUrl cached = new CachedShortUrl(shortUrl.getOriginalUrl(), shortUrl.getExpiresAt());
            String raw = objectMapper.writeValueAsString(cached);

            Duration ttl = defaultTtl;
            if (shortUrl.getExpiresAt() != null) {
                Duration untilExpiry = Duration.between(Instant.now(), shortUrl.getExpiresAt());
                if (untilExpiry.isNegative() || untilExpiry.isZero()) {
                    return;
                }
                ttl = untilExpiry;
            }

            redis.opsForValue().set(PREFIX + shortUrl.getCode(), raw, ttl);
        } catch (Exception ignored) {
        }
    }
}

