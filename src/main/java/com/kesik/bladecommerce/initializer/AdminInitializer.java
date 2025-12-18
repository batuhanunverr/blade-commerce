package com.kesik.bladecommerce.initializer;

import com.kesik.bladecommerce.entity.Admin;
import com.kesik.bladecommerce.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Create default admin if it doesn't exist
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setEmail("admin@kesik.com");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());

            adminRepository.save(admin);

            log.info("==========================================================");
            log.info("Default admin user created successfully!");
            log.info("Username: admin");
            log.info("Password: Admin123!");
            log.info("⚠️  IMPORTANT: Change this password after first login!");
            log.info("==========================================================");
        } else {
            log.info("Admin user already exists. Skipping initialization.");
        }
    }
}
