package com.example.talking.webSocket.controllers;

import com.example.talking.webSocket.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{roomName}")  // Обрабатываем сообщения в комнате
    public void sendMessage(@DestinationVariable String roomName, ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomName, message);
    }
}