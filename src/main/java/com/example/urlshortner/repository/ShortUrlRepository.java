package com.example.urlshortner.repository;

import com.example.urlshortner.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByCode(String code);

    @Query("""
            select s from ShortUrl s
            where s.code = :code
              and (s.expiresAt is null or s.expiresAt > :now)
            """)
    Optional<ShortUrl> findActiveByCode(@Param("code") String code, @Param("now") Instant now);

    @Modifying
    @Query("""
            update ShortUrl s
               set s.clickCount = s.clickCount + 1
             where s.code = :code
               and (s.expiresAt is null or s.expiresAt > :now)
            """)
    int incrementClickCountIfActive(@Param("code") String code, @Param("now") Instant now);
}

