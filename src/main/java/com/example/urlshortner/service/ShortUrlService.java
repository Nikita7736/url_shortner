package com.example.urlshortner.service;

import com.example.urlshortner.model.ShortUrl;
import com.example.urlshortner.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class ShortUrlService {

    private final ShortUrlRepository repository;
    private final Base62Encoder encoder;
    private final ShortUrlCache cache;

    public ShortUrlService(ShortUrlRepository repository, ShortUrlCache cache) {
        this.repository = repository;
        this.encoder = new Base62Encoder();
        this.cache = cache;
    }

    @Transactional
    public ShortUrl createShortUrl(String originalUrl, Duration ttl) {
        validateUrl(originalUrl);
        Instant now = Instant.now();
        Instant expiresAt = ttl != null ? now.plus(ttl) : null;

        ShortUrl entity = new ShortUrl();
        entity.setOriginalUrl(originalUrl);
        entity.setCreatedAt(now);
        entity.setExpiresAt(expiresAt);
        entity.setClickCount(0L);

        // Persist first to get ID, then generate code from ID for scalability.
        ShortUrl saved = repository.save(entity);
        String code = encoder.encode(saved.getId());
        saved.setCode(code);
        ShortUrl finalSaved = repository.save(saved);
        cache.put(finalSaved);
        return finalSaved;
    }

    private void validateUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }

    @Transactional
    public Optional<String> resolveAndIncrementOriginalUrl(String code) {
        Instant now = Instant.now();

        Optional<ShortUrlCache.CachedShortUrl> cached = cache.get(code);
        if (cached.isPresent()) {
            int updated = repository.incrementClickCountIfActive(code, now);
            return updated == 1 ? Optional.of(cached.get().originalUrl()) : Optional.empty();
        }

        Optional<ShortUrl> active = repository.findActiveByCode(code, now);
        if (active.isEmpty()) {
            return Optional.empty();
        }

        ShortUrl shortUrl = active.get();
        cache.put(shortUrl);
        int updated = repository.incrementClickCountIfActive(code, now);
        if (updated != 1) {
            return Optional.empty();
        }
        return Optional.of(shortUrl.getOriginalUrl());
    }

    @Transactional(readOnly = true)
    public Optional<ShortUrl> getByCode(String code) {
        return repository.findActiveByCode(code, Instant.now());
    }
}

