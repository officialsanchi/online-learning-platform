package com.example.AuthenticationApplicationSystemBackend.controllers;

import com.example.AuthenticationApplicationSystemBackend.dto.ChangePasswordRequest;
import com.example.AuthenticationApplicationSystemBackend.dto.MessageResponse;
import com.example.AuthenticationApplicationSystemBackend.dto.UpdateProfileRequest;
import com.example.AuthenticationApplicationSystemBackend.dto.UserResponse;
import com.example.AuthenticationApplicationSystemBackend.services.FileStorageService;
import com.example.AuthenticationApplicationSystemBackend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController

@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getCurrentUserProfile(username));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.convertToUserResponse(
                userService.getUserById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }

    @PostMapping("/profile/photo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> uploadProfilePhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.uploadProfilePhoto(username, file));
    }

    @GetMapping("/profile/photo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getProfilePhoto(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = userService.getCurrentUserProfile(username);

        if (user.getProfilePhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageData = fileStorageService.loadFile(user.getProfilePhoto());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"profile.jpg\"")
                .body(imageData);
    }

    @DeleteMapping("/profile/photo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteProfilePhoto(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteProfilePhoto(username);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Profile photo deleted successfully")
                .build());
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.changePassword(username, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("User deleted successfully")
                .build());
    }
}
