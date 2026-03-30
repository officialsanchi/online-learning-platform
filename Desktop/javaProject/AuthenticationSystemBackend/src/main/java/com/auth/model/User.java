package com.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User   {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    private boolean emailVerified = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLoginAt;
    private String emailVerificationToken;
    private LocalDateTime emailVerificationTokenExpiry;
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;
    private int failedLoginAttempts = 0;
    private LocalDateTime accountLockedUntil;
    private boolean twoFactorEnabled = false;
    private String twoFactorSecret;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;



}
