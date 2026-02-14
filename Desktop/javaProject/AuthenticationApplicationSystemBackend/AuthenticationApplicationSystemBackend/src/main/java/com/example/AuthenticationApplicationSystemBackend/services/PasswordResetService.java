package com.example.AuthenticationApplicationSystemBackend.services;

import com.example.AuthenticationApplicationSystemBackend.dto.ForgotPasswordRequest;
import com.example.AuthenticationApplicationSystemBackend.dto.MessageResponse;
import com.example.AuthenticationApplicationSystemBackend.dto.ResetPasswordRequest;
import com.example.AuthenticationApplicationSystemBackend.entity.PasswordResetToken;
import com.example.AuthenticationApplicationSystemBackend.entity.User;
import com.example.AuthenticationApplicationSystemBackend.repository.PasswordResetTokenRepository;
import com.example.AuthenticationApplicationSystemBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
@Slf4j

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private static final int TOKEN_EXPIRY_HOURS = 24;

    public MessageResponse initiatePasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email"));

        // Delete any existing tokens
        tokenRepository.deleteByUser(user);

        // Generate reset token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Send reset email
        sendPasswordResetEmail(user.getEmail(), token);

        return MessageResponse.builder()
                .message("Password reset instructions sent to your email")
                .build();
    }

    public MessageResponse resetPassword(ResetPasswordRequest request) {
        // Verify passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Find and validate token
        PasswordResetToken resetToken = tokenRepository
                .findValidToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        User user = resetToken.getUser();

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getUsername());

        return MessageResponse.builder()
                .message("Password reset successfully")
                .build();
    }

    private void sendPasswordResetEmail(String email, String token) {
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;

        String subject = "Password Reset Request";
        String body = String.format("""
            You requested to reset your password.
            
            Click the link below to reset your password:
            %s
            
            This link will expire in %d hours.
            
            If you did not request this, please ignore this email.
            
            Best regards,
            Your Application Team
            """, resetLink, TOKEN_EXPIRY_HOURS);

        emailService.sendEmail(email, subject, body);
    }

    @Scheduled(cron = "0 0 2 * * *") // Run at 2 AM daily
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredOrUsedTokens(LocalDateTime.now());
        log.info("Cleaned up expired password reset tokens");
    }
}
