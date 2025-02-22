package com.example.talking.service;

import com.example.talking.entity.UserEntity;
import com.example.talking.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    private static final long ACCESS_EXPIRATION = 1000 * 60 * 15; // 15 минут
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 дней
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserEntity user) {
        return generateToken(user, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(UserEntity user) {
        String refreshToken = generateToken(user, REFRESH_EXPIRATION);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return refreshToken;
    }

    private String generateToken(UserEntity user, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Optional<UserEntity> findUserByToken(String token) {
        String email = extractClaims(token).getSubject();
        return userRepository.findByEmail(email);
    }

    public String refreshAccessToken(String refreshToken) {
        log.info("Received refresh token: {}", refreshToken);

        if (!validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Optional<UserEntity> user = findUserByToken(refreshToken);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return generateAccessToken(user.get());
    }
}