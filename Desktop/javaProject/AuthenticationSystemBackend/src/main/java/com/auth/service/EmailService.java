package com.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@authsystem.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendEmailVerification(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Email Verification - Authentication System");
        message.setText(buildEmailVerificationMessage(token));
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset - Authentication System");
        message.setText(buildPasswordResetMessage(token));
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Welcome to Authentication System");
        message.setText(buildWelcomeMessage(firstName));
        mailSender.send(message);
    }

    public void sendAccountLockedEmail(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Account Locked - Authentication System");
        message.setText(buildAccountLockedMessage(firstName));
        mailSender.send(message);
    }

    public void sendPasswordChangedEmail(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Changed - Authentication System");
        message.setText(buildPasswordChangedMessage(firstName));
        mailSender.send(message);
    }

    private String buildEmailVerificationMessage(String token) {
        return "Dear User,\n\n" +
               "Thank you for registering with our Authentication System!\n\n" +
               "Please click the link below to verify your email address:\n" +
               frontendUrl + "/verify-email?token=" + token + "\n\n" +
               "This link will expire in 24 hours.\n\n" +
               "If you did not create an account, please ignore this email.\n\n" +
               "Best regards,\n" +
               "Authentication System Team";
    }

    private String buildPasswordResetMessage(String token) {
        return "Dear User,\n\n" +
               "You have requested to reset your password.\n\n" +
               "Please click the link below to reset your password:\n" +
               frontendUrl + "/reset-password?token=" + token + "\n\n" +
               "This link will expire in 1 hour.\n\n" +
               "If you did not request this password reset, please ignore this email.\n\n" +
               "Best regards,\n" +
               "Authentication System Team";
    }

    private String buildWelcomeMessage(String firstName) {
        return "Dear " + firstName + ",\n\n" +
               "Welcome to our Authentication System!\n\n" +
               "Your email has been successfully verified and your account is now active.\n\n" +
               "You can now log in and start using our services.\n\n" +
               "Best regards,\n" +
               "Authentication System Team";
    }

    private String buildAccountLockedMessage(String firstName) {
        return "Dear " + firstName + ",\n\n" +
               "Your account has been temporarily locked due to multiple failed login attempts.\n\n" +
               "For security reasons, your account will be automatically unlocked after 30 minutes.\n\n" +
               "If you believe this was not you, please contact our support team immediately.\n\n" +
               "Best regards,\n" +
               "Authentication System Team";
    }

    private String buildPasswordChangedMessage(String firstName) {
        return "Dear " + firstName + ",\n\n" +
               "Your password has been successfully changed.\n\n" +
               "If you did not make this change, please contact our support team immediately.\n\n" +
               "Best regards,\n" +
               "Authentication System Team";
    }
}
