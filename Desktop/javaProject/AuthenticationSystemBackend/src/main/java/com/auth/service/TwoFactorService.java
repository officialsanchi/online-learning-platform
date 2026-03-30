package com.auth.service;

import com.auth.model.User;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TwoFactorService {

    private static final int BACKUP_CODE_COUNT = 10;
    private static final int BACKUP_CODE_LENGTH = 8;
    private static final int TOTP_CODE_LENGTH = 6;

    private final GoogleAuthenticator googleAuthenticator;
    private final SecureRandom secureRandom;

    public TwoFactorService(GoogleAuthenticator googleAuthenticator, SecureRandom secureRandom) {
        this.googleAuthenticator = googleAuthenticator;
        this.secureRandom = secureRandom;
    }

    public String generateSecretKey() {
        return googleAuthenticator.createCredentials().getKey();
    }

    public String generateQRCodeUrl(GoogleAuthenticatorKey secret, String userEmail, String issuer) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, userEmail, secret);
    }

    public boolean verifyCode(String secret, int code) {
        return googleAuthenticator.authorize(secret, code);
    }

    public List<String> generateBackupCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < BACKUP_CODE_COUNT; i++) {
            codes.add(generateRandomCode());
        }
        return codes;
    }
    public boolean verifyBackupCode(User user, String inputCode) {
        if (inputCode == null || user.getBackupCodes() == null) return false;

        List<String> codes = new ArrayList<>(List.of(user.getBackupCodes().split(",")));

        if (codes.contains(inputCode)) {
            codes.remove(inputCode); // consume the backup code
            user.setBackupCodes(String.join(",", codes));  // save updated list
            return true;
        }

        return false;
    }


    public boolean hasValidBackupCodes(User user) {
        return user.getBackupCodes() != null && !user.getBackupCodes().isEmpty();
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < BACKUP_CODE_LENGTH; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }

    public boolean isValidTwoFactorCode(String code) {
        return code != null && (code.matches("\\d{6}") || code.matches("\\d{8}"));
    }

    public boolean verifyTwoFactorAuthentication(User user, String code) {
        if (!user.isTwoFactorEnabled() || code == null) return false;

        if (code.matches("\\d{6}")) {
            return verifyCode(user.getTwoFactorSecret(), Integer.parseInt(code));
        }
        if (code.matches("\\d{8}")) {
            return verifyBackupCode(user, code);
        }
        return false;
    }
}
