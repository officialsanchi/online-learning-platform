package com.example.AuthenticationApplicationSystemBackend.repository;

import com.example.AuthenticationApplicationSystemBackend.entity.Role;
import com.example.AuthenticationApplicationSystemBackend.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
