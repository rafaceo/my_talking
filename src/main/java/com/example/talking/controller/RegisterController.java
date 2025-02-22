package com.example.talking.controller;

import com.example.talking.dto.RegisterRequest;
import com.example.talking.entity.UserEntity;
import com.example.talking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterRequest request) {
        UserEntity newUser = userService.register(request);
        return ResponseEntity.ok(newUser);
    }

}
