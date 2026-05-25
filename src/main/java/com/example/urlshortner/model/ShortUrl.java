package com.example.urlshortner.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "short_urls", indexes = {
        @Index(name = "idx_short_url_code", columnList = "code", unique = true),
        @Index(name = "idx_short_url_expires_at", columnList = "expiresAt"),
        @Index(name = "idx_short_url_created_at", columnList = "createdAt")
})
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 16)
    private String code;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant expiresAt;

    @Column(nullable = false)
    private long clickCount;

    public ShortUrl() {
    }

    public ShortUrl(String code, String originalUrl, Instant createdAt, Instant expiresAt) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.clickCount = 0L;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }
}

