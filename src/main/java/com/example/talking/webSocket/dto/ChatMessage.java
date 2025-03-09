package com.example.talking.webSocket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String user;
    private String message;
    private String timestamp;
}

