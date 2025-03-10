package com.example.talking.controller;

import com.example.talking.dto.RoomCreateRequest;
import com.example.talking.entity.MessageEntity;
import com.example.talking.entity.RoomEntity;
import com.example.talking.service.MessageService;
import com.example.talking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<String> createRoom(@RequestBody RoomCreateRequest request) {
        roomService.createRoom(request);
        return ResponseEntity.ok("Комната создана");
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinRoom(@RequestParam String name, @RequestParam String password) {
        boolean success = roomService.joinRoom(name, password);
        if (success) {
            return ResponseEntity.ok("Вход в комнату успешен");
        } else {
            return ResponseEntity.status(401).body("Неверные данные");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<RoomEntity>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

}
