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
        log.debug("Инициализация JwtService...");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.debug("Секретный ключ успешно инициализирован.");
    }

    public String generateAccessToken(UserEntity user) {
        log.debug("Генерация access-токена для пользователя: {}", user.getEmail());
        return generateToken(user, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(UserEntity user) {
        log.debug("Генерация refresh-токена для пользователя: {}", user.getEmail());
        String refreshToken = generateToken(user, REFRESH_EXPIRATION);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        log.debug("Refresh-токен сохранен в БД.");
        return refreshToken;
    }

    private String generateToken(UserEntity user, long expiration) {
        log.debug("Формирование JWT токена для пользователя: {}, срок действия: {} мс", user.getEmail(), expiration);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();

        log.debug("JWT токен успешно создан: {}", token);
        return token;
    }

    public boolean validateToken(String token) {
        log.debug("Проверка валидности токена: {}", token);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.debug("Токен валиден.");
            return true;
        } catch (Exception e) {
            log.error("Ошибка валидации токена: {}", e.getMessage());
            return false;
        }
    }

    public String getUserFromToken(String token) {
        log.debug("Извлечение пользователя из токена: {}", token);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String email = claims.getSubject();
            log.debug("Пользователь в токене: {}", email);
            return email;
        } catch (Exception e) {
            log.error("Ошибка при разборе токена: {}", e.getMessage());
            return null;
        }
    }

    public Claims extractClaims(String token) {
        log.debug("Извлечение claims из токена: {}", token);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("Claims успешно извлечены: {}", claims);
            return claims;
        } catch (Exception e) {
            log.error("Ошибка извлечения claims: {}", e.getMessage());
            return null;
        }
    }

    public Optional<UserEntity> findUserByToken(String token) {
        log.debug("Поиск пользователя по токену...");
        String email = extractClaims(token).getSubject();
        log.debug("Email из токена: {}", email);
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            log.debug("Пользователь найден: {}", user.get().getEmail());
        } else {
            log.error("Пользователь с email {} не найден.", email);
        }
        return user;
    }

    public String refreshAccessToken(String refreshToken) {
        log.debug("Запрос на обновление access-токена с refresh-токеном: {}", refreshToken);

        if (!validateToken(refreshToken)) {
            log.error("Недействительный refresh-токен!");
            throw new RuntimeException("Invalid refresh token");
        }

        Optional<UserEntity> user = findUserByToken(refreshToken);
        if (user.isEmpty()) {
            log.error("Пользователь не найден, невозможно обновить токен.");
            throw new RuntimeException("User not found");
        }
        String newAccessToken = generateAccessToken(user.get());
        log.debug("Новый access-токен сгенерирован: {}", newAccessToken);
        return newAccessToken;
    }
}
