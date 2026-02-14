package com.example.AuthenticationApplicationSystemBackend.repository;

import com.example.AuthenticationApplicationSystemBackend.entity.PasswordResetToken;
import com.example.AuthenticationApplicationSystemBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Query("SELECT p FROM PasswordResetToken p WHERE p.token = :token AND p.used = false " +
            "AND p.expiryDate > :now")
    Optional<PasswordResetToken> findValidToken(String token, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now OR p.used = true")
    void deleteExpiredOrUsedTokens(LocalDateTime now);

    void deleteByUser(User user);
}
