package com.example.AuthenticationApplicationSystemBackend.repository;

import com.example.AuthenticationApplicationSystemBackend.entity.OtpType;
import com.example.AuthenticationApplicationSystemBackend.entity.OtpVerification;
import com.example.AuthenticationApplicationSystemBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByUserAndOtpCodeAndType(
            User user,
            String otpCode,
            OtpType type
    );

    Optional<OtpVerification> findByUserAndTypeAndVerifiedFalse(
            User user,
            OtpType type
    );

    List<OtpVerification> findByUserAndType(
            User user,
            OtpType type
    );

    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.expiryTime < :now")
    void deleteExpiredOtps(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.user = :user AND o.type = :type")
    void deleteByUserAndType(User user, OtpType type);

}
