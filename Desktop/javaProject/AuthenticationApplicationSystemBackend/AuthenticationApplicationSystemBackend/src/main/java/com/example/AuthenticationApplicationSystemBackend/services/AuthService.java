package com.example.AuthenticationApplicationSystemBackend.services;

import com.example.AuthenticationApplicationSystemBackend.dto.*;
import com.example.AuthenticationApplicationSystemBackend.entity.*;
import com.example.AuthenticationApplicationSystemBackend.repository.RoleRepository;
import com.example.AuthenticationApplicationSystemBackend.repository.UserRepository;
import com.example.AuthenticationApplicationSystemBackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final EmailService emailService;

    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .enabled(false)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .roles(new HashSet<>())
                .build();

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        otpService.generateAndSendOtp(savedUser, OtpType.EMAIL_VERIFICATION);

        return ApiResponse.builder()
                .success(true)
                .message("Registration successful! Please check your email to verify your account.")
                .data(convertToUserResponse(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(),
                request.getUsernameOrEmail()
        ).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.getAccountNonLocked()) {
            throw new IllegalArgumentException("Account is locked due to multiple failed login attempts");
        }
        if (!user.getEnabled()) {
            throw new IllegalArgumentException("Account is not verified. Please verify your email.");
        }

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            user.setFailedLoginAttempts(0);
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);

            String accessToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = refreshTokenService.createRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toSet()))
                    .expiresAt(jwtUtil.extractExpirationAsLocalDateTime(accessToken))
                    .build();

        } catch (Exception e) {

            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountNonLocked(false);
                userRepository.save(user);
                throw new IllegalArgumentException("Account locked due to multiple failed login attempts");
            }

            userRepository.save(user);
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.refreshAccessToken(request.getRefreshToken());
    }

    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        refreshTokenService.deleteByUser(user);
    }

    public ApiResponse verifyEmail(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean verified = otpService.verifyOtp(
                user,
                request.getOtpCode(),
                OtpType.EMAIL_VERIFICATION
        );

        if (verified) {
            user.setEnabled(true);
            userRepository.save(user);

            return ApiResponse.builder()
                    .success(true)
                    .message("Email verified successfully! You can now login.")
                    .build();
        } else {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
    }

    public MessageResponse resendVerificationOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getEnabled()) {
            throw new IllegalArgumentException("Email is already verified");
        }

        otpService.generateAndSendOtp(user, OtpType.EMAIL_VERIFICATION);

        return MessageResponse.builder()
                .message("Verification OTP sent to your email")
                .build();
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .profilePhoto(user.getProfilePhoto())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .enabled(user.getEnabled())
                .lastLoginDate(user.getLastLoginDate())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
