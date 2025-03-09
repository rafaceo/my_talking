package com.example.talking.repository;

import com.example.talking.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByRoomNameOrderByTimestampAsc(String roomName);
}
