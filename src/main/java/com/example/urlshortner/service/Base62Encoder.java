package com.example.urlshortner.service;

public class Base62Encoder {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();

    public String encode(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Value must be positive");
        }
        StringBuilder sb = new StringBuilder();
        long current = value;
        while (current > 0) {
            int remainder = (int) (current % BASE);
            sb.append(ALPHABET.charAt(remainder));
            current /= BASE;
        }
        return sb.reverse().toString();
    }
}

