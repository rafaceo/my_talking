package com.example.talking.controller;

import com.example.talking.dto.AuthRequest;
import com.example.talking.dto.RefreshTokenRequest;
import com.example.talking.dto.response.AuthResponse;
import com.example.talking.entity.UserEntity;
import com.example.talking.repository.UserRepository;
import com.example.talking.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Optional<UserEntity> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(401).build();
        }
        String accessToken = jwtService.generateAccessToken(user.get());
        String refreshToken = jwtService.generateRefreshToken(user.get());
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        Logger logger = LoggerFactory.getLogger(AuthController.class);

        String refreshToken = request.getRefreshToken();
        logger.info("Получен refreshToken: {}", refreshToken);

        Optional<UserEntity> user = userRepository.findByRefreshToken(refreshToken);

        if (user.isEmpty()) {
            logger.warn("Пользователь с таким refreshToken не найден!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Текущий refreshToken в БД: {}", user.get().getRefreshToken());

        String newAccessToken = jwtService.generateAccessToken(user.get());
        String newRefreshToken = jwtService.generateRefreshToken(user.get());

        user.get().setRefreshToken(newRefreshToken);
        userRepository.save(user.get());

        logger.info("Выдан новый refreshToken: {}", newRefreshToken);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }
}

