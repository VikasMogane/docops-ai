package com.docops.user.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiry}")
    private long expiry;

    public String generateToken(Long userId, Long orgId, String role) {

        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("orgId", orgId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))   // ✅ subject = userId
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(key)
                .compact();
    }
}
