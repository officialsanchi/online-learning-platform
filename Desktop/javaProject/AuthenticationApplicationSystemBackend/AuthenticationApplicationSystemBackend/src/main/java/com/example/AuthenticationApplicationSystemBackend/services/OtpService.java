package com.example.AuthenticationApplicationSystemBackend.services;

import com.example.AuthenticationApplicationSystemBackend.entity.OtpType;
import com.example.AuthenticationApplicationSystemBackend.entity.OtpVerification;
import com.example.AuthenticationApplicationSystemBackend.entity.User;
import com.example.AuthenticationApplicationSystemBackend.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static com.example.AuthenticationApplicationSystemBackend.entity.OtpType.*;

@Slf4j

@Service
@RequiredArgsConstructor
@Transactional
public class OtpService {
    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;
    private static final int OTP_EXPIRY_MINUTES = 10;

    public void generateAndSendOtp(User user, OtpType type) {
        // Delete any existing OTPs for this user and type
        otpRepository.deleteByUserAndType(user, type);

        // Generate 6-digit OTP
        String otpCode = generateOtp();

        // Create OTP verification record
        OtpVerification otp = OtpVerification.builder()
                .user(user)
                .otpCode(otpCode)
                .type(type)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .verified(false)
                .build();

        otpRepository.save(otp);

        // Send OTP via email
        sendOtpEmail(user.getEmail(), otpCode, type);

        log.info("OTP generated for user: {} of type: {}", user.getUsername(), type);
    }


    public boolean verifyOtp(User user, String otpCode, OtpType type) {
        OtpVerification otp = otpRepository.findByUserAndOtpCodeAndType(user, otpCode, type)
                .orElse(null);

        if (otp == null) {
            log.warn("OTP not found for user: {}", user.getUsername());
            return false;
        }

        if (otp.isExpired()) {
            log.warn("OTP expired for user: {}", user.getUsername());
            return false;
        }

        if (otp.getVerified()) {
            log.warn("OTP already used for user: {}", user.getUsername());
            return false;
        }

        // Mark OTP as verified
        otp.setVerified(true);
        otpRepository.save(otp);

        log.info("OTP verified successfully for user: {}", user.getUsername());
        return true;
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOtpEmail(String email, String otpCode,OtpType type) {
        String subject = getOtpEmailSubject(type);
        String body = getOtpEmailBody(otpCode, type);
        emailService.sendEmail(email, subject, body);
    }

    private String getOtpEmailSubject(OtpType type) {
        return switch (type) {
            case EMAIL_VERIFICATION -> "Email Verification - OTP Code";
            case PASSWORD_RESET -> "Password Reset - OTP Code";
            case TWO_FACTOR_AUTH -> "Two-Factor Authentication - OTP Code";
            case PHONE_VERIFICATION -> "Phone Verification - OTP Code";
        };
    }

    private String getOtpEmailBody(String otpCode, OtpType type) {
        return String.format("""
            Your OTP code is: %s
            
            This code will expire in %d minutes.
            
            If you did not request this code, please ignore this email.
            
            Best regards,
            Your Application Team
            """, otpCode, OTP_EXPIRY_MINUTES);
    }

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    public void cleanupExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
        log.info("Cleaned up expired OTPs");
    }
}
