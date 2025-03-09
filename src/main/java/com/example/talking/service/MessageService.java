package com.example.talking.service;

import com.example.talking.entity.MessageEntity;
import com.example.talking.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public void saveMessage(String username, String text, String roomName) {
        MessageEntity message = new MessageEntity();
        message.setUsername(username);
        message.setText(text);
        message.setRoomName(roomName);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

    public List<MessageEntity> getMessages(String roomName) {
        return messageRepository.findByRoomNameOrderByTimestampAsc(roomName);
    }
}

