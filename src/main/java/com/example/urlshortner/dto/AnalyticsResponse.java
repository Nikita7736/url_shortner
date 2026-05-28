package com.example.urlshortner.dto;

public record AnalyticsResponse(String code, String originalUrl, long clickCount, String createdAt,
                                String expiresAt) {
}


