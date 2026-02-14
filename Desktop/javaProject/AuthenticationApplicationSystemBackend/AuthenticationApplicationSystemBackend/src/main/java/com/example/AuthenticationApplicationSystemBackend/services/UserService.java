package com.example.AuthenticationApplicationSystemBackend.services;

import com.example.AuthenticationApplicationSystemBackend.dto.ChangePasswordRequest;
import com.example.AuthenticationApplicationSystemBackend.dto.MessageResponse;
import com.example.AuthenticationApplicationSystemBackend.dto.UpdateProfileRequest;
import com.example.AuthenticationApplicationSystemBackend.dto.UserResponse;
import com.example.AuthenticationApplicationSystemBackend.entity.User;
import com.example.AuthenticationApplicationSystemBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@Service
@RequiredArgsConstructor
@Transactional

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public UserResponse getCurrentUserProfile(String username) {
        User user = getUserByUsername(username);
        return convertToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = getUserByUsername(username);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    public UserResponse uploadProfilePhoto(String username, MultipartFile file) {
        User user = getUserByUsername(username);

        // Delete old photo if exists
        if (user.getProfilePhoto() != null) {
            fileStorageService.deleteFile(user.getProfilePhoto());
        }

        // Save new photo
        String fileName = fileStorageService.storeFile(file, "profile-photos");
        user.setProfilePhoto(fileName);

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    public void deleteProfilePhoto(String username) {
        User user = getUserByUsername(username);

        if (user.getProfilePhoto() != null) {
            fileStorageService.deleteFile(user.getProfilePhoto());
            user.setProfilePhoto(null);
            userRepository.save(user);
        }
    }

    public MessageResponse changePassword(String username, ChangePasswordRequest request) {
        User user = getUserByUsername(username);

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Verify new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        // Verify new password is different from current
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return MessageResponse.builder()
                .message("Password changed successfully")
                .build();
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);

        // Delete profile photo if exists
        if (user.getProfilePhoto() != null) {
            fileStorageService.deleteFile(user.getProfilePhoto());
        }

        userRepository.deleteById(id);
    }

    public UserResponse convertToUserResponse(User user) {
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
