package com.example.urlshortner.dto;

public record ShortUrlResponse(String code, String shortUrl, String originalUrl, Long ttlSeconds) {
}

