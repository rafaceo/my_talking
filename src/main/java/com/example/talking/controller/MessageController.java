package com.example.talking.controller;


import com.example.talking.entity.MessageEntity;
import com.example.talking.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{roomName}/messages")
    public ResponseEntity<List<MessageEntity>> getMessages(@PathVariable String roomName) {
        List<MessageEntity> messages = messageService.getMessages(roomName);
        return ResponseEntity.ok(messages);
    }
}
