package com.example.talking.dto;

import lombok.Data;

@Data
public class RoomCreateRequest {
    private String name;
    private String password;
    private String confirmPassword;
}

