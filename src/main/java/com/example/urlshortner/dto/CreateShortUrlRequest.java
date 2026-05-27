package com.example.urlshortner.dto;

public record CreateShortUrlRequest(String url, Long ttlSeconds) {
}

