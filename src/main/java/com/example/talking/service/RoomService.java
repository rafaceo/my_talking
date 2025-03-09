package com.example.talking.service;

import com.example.talking.dto.RoomCreateRequest;
import com.example.talking.entity.RoomEntity;
import com.example.talking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public RoomEntity createRoom(RoomCreateRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        RoomEntity room = new RoomEntity();
        room.setName(request.getName());
        room.setPassword(passwordEncoder.encode(request.getPassword()));

        return roomRepository.save(room);
    }

    public boolean joinRoom(String name, String password) {
        Optional<RoomEntity> roomOpt = roomRepository.findByName(name);
        if (roomOpt.isPresent()) {
            RoomEntity room = roomOpt.get();
            return passwordEncoder.matches(password, room.getPassword());
        }
        return false;
    }

    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

}
