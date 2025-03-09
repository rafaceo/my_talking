package com.example.talking.webSocket.handlers;

import com.example.talking.entity.MessageEntity;
import com.example.talking.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            String roomName = getRoomName(session);
            if (roomName == null || roomName.isEmpty()) {
                System.out.println("Ошибка: комната не найдена");
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            List<MessageEntity> messages = messageService.getMessages(roomName);
            for (MessageEntity message : messages) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            }

            sessions.put(session.getId(), session);
            System.out.println("WebSocket подключен: " + session.getId());
        } catch (Exception e) {
            System.err.println("Ошибка при установке соединения: " + e.getMessage());
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        String payload = message.getPayload().toString();
        MessageEntity receivedMessage = objectMapper.readValue(payload, MessageEntity.class);

        messageService.saveMessage(receivedMessage.getUsername(), receivedMessage.getText(), getRoomName(session));

        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Ошибка WebSocket: " + exception.getMessage());
    }

    private String getRoomName(WebSocketSession session) {
        String path = session.getUri().getPath(); // "/chat/Eclipse"
        return path.substring(path.lastIndexOf("/") + 1); // "Eclipse"
    }

}

