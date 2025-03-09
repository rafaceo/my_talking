//package com.example.talking.controller;
//
//import com.example.talking.entity.MessageEntity;
//import com.example.talking.service.MessageService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/chat")
//public class ChatController {
//
//    private final MessageService messageService;
//
//    public ChatController(MessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    @GetMapping("/{roomName}/messages")
//    public List<MessageEntity> getChatHistory(@PathVariable String roomName) {
//        return messageService.getMessages(roomName);
//    }
//}
