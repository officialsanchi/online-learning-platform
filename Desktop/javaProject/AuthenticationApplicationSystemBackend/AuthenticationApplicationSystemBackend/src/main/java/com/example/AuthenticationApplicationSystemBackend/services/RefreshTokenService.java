package com.example.AuthenticationApplicationSystemBackend.services;

import com.example.AuthenticationApplicationSystemBackend.dto.AuthResponse;
import com.example.AuthenticationApplicationSystemBackend.entity.RefreshToken;
import com.example.AuthenticationApplicationSystemBackend.entity.User;
import com.example.AuthenticationApplicationSystemBackend.repository.RefreshTokenRepository;
import com.example.AuthenticationApplicationSystemBackend.repository.UserRepository;
import com.example.AuthenticationApplicationSystemBackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refreshTokenExpiration:604800000}") // 7 days
    private Long refreshTokenExpiration;

    public String createRefreshToken(User user) {
        // Delete existing refresh token for user
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        // Generate new refresh token
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public AuthResponse refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        // Generate new access token
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .expiresAt(jwtUtil.extractExpirationAsLocalDateTime(accessToken))
                .build();
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
    }

    @Scheduled(cron = "0 0 3 * * *") // Run at 3 AM daily
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up expired refresh tokens");
    }
}
