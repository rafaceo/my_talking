package com.example.talking.service;

import com.example.talking.dto.RegisterRequest;
import com.example.talking.entity.UserEntity;
import com.example.talking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserEntity register(RegisterRequest request){
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .refresh_token(null)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }


}
