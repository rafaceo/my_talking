package com.example.talking.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
