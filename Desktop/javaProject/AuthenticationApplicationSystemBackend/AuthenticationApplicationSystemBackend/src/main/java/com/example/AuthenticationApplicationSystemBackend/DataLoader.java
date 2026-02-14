package com.example.AuthenticationApplicationSystemBackend;

import com.example.AuthenticationApplicationSystemBackend.entity.Role;
import com.example.AuthenticationApplicationSystemBackend.entity.RoleType;
import com.example.AuthenticationApplicationSystemBackend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Initialize default roles if they don't exist
        createRoleIfNotExists(RoleType.ROLE_USER, "Default user role");
        createRoleIfNotExists(RoleType.ROLE_ADMIN, "Administrator role");
        createRoleIfNotExists(RoleType.ROLE_MODERATOR, "Moderator role");

        log.info("Data initialization completed");
    }

    private void createRoleIfNotExists(RoleType roleName, String description) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .description(description)
                    .build();
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        }
    }
}
