package com.auth.service;

import com.auth.dto.request.*;
import com.auth.dto.response.*;
import com.auth.model.Role;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private EmailService emailService;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    public RegisterResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }


        if (userRepository.existsByUsername(request.getUserName())) {
            throw new RuntimeException("Username is already taken!");
        }


        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use!");
        }


        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);


        return new RegisterResponse(
                savedUser.getId().toString(),
                savedUser.getEmail(),
                "User registered successfully. Please verify your email.",
                "SUCCESS"
        );
    }


    public LoginResponse authenticate(LoginRequest request) {
        String identifier = request.getIdentifier();
        User user;


        if (identifier.contains("@")) {

            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        } else if (identifier.matches("\\d+")) {

            user = userRepository.findByPhoneNumber(identifier)
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        } else {

            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        }


        if (user.isAccountLocked()) {
            throw new RuntimeException("Account is temporarily locked due to too many failed attempts. Please try again later.");
        }

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
            );


            user.resetFailedAttempts();
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);


            if (user.isTwoFactorEnabled()) {
                return new LoginResponse(null, null, "Bearer", "2FA_REQUIRED");
            }


            String accessToken = jwtService.generateToken((UserDetails) user);
            String refreshToken = jwtService.generateRefreshToken((UserDetails) user);

            return new LoginResponse(accessToken, refreshToken, "Bearer", "3600s");

        } catch (BadCredentialsException e) {

            user.getIncrementFailedAttempts();

            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.lockAccount(LOCK_DURATION_MINUTES);
                emailService.sendAccountLockedEmail(user.getEmail(), user.getFirstName());
            }

            userRepository.save(user);
            throw new BadCredentialsException("Invalid email/username/phone or password");
        }
    }


    public AuthResponse verifyTwoFactor(String email, TwoFactorRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is not enabled for this user");
        }



        if (request.getCode().matches("\\d{8}")) {
            userRepository.save(user);
        }


        String accessToken = jwtService.generateToken((UserDetails) user);
        String refreshToken = jwtService.generateRefreshToken((UserDetails) user);

        return new AuthResponse(accessToken, refreshToken, "2FA verification successful");
    }

    public MessageResponse verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        userRepository.save(user);


        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        return new MessageResponse("Email verified successfully");
    }

    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        String identifier = request.getIdentifier(); // can be email, username, or phone
        User user;

        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found with this email"));
        } else if (identifier.matches("\\d+")) {
            user = userRepository.findByPhoneNumber(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found with this phone number"));
        } else {
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found with this username"));
        }


        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        if (user.getEmail() != null) {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        }


        return new MessageResponse("Password reset instructions sent to your registered email/phone.");
    }


    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (user.getPasswordResetTokenExpiry() == null ||
                user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }


        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.resetFailedAttempts();
        userRepository.save(user);


        emailService.sendPasswordChangedEmail(user.getEmail(), user.getFirstName());

        return new MessageResponse("Password reset successfully");
    }

    public MessageResponse changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUser();


        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        emailService.sendPasswordChangedEmail(currentUser.getEmail(), currentUser.getFirstName());

        return new MessageResponse("Password changed successfully");
    }

    public TwoFactorSetupResponse setupTwoFactor() {
        User currentUser = getCurrentUser();

        if (currentUser.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is already enabled for this user");
        }


        return null;
    }

    public MessageResponse enableTwoFactor(TwoFactorRequest request) {
        User currentUser = getCurrentUser();

        if (currentUser.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is already enabled");
        }

        currentUser.setTwoFactorEnabled(true);
        userRepository.save(currentUser);

        return new MessageResponse("2FA enabled successfully");
    }

    public MessageResponse disableTwoFactor(TwoFactorRequest request) {
        User currentUser = getCurrentUser();

        if (!currentUser.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is not enabled");
        }

        currentUser.setTwoFactorEnabled(false);
        currentUser.setTwoFactorSecret(null);
        currentUser.setBackupCodes(null);
        userRepository.save(currentUser);

        return new MessageResponse("2FA disabled successfully");
    }

    public TwoFactorSetupResponse regenerateBackupCodes() {
        User currentUser = getCurrentUser();

        if (!currentUser.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is not enabled");
        }

        return null;


    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAllActiveUsers().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserResponse(user);
    }

    public UserResponse getUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already in use");
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false);
        }

        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    public MessageResponse deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
        return new MessageResponse("User deleted successfully");
    }

    public UserResponse updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));


        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    public UserResponse getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        return convertToUserResponse(currentUser);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isEmailVerified(),
                user.isTwoFactorEnabled(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }
}
