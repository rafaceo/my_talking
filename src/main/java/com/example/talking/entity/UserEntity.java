package com.example.talking.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "email", nullable = false,unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    private String refresh_token;
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;
}
