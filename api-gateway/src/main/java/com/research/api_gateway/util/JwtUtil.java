package com.research.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        log.info("JWT Secret key initialized successfully");
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {

            log.error("Failed to parse JWT claims: {}", e.getMessage());
            throw e;
        }
    }

    // Extract email (subject)
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Extract userId claim
    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }
}