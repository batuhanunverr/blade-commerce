package com.kesik.bladecommerce.service.impl;

import com.kesik.bladecommerce.dto.auth.LoginRequest;
import com.kesik.bladecommerce.dto.auth.LoginResponse;
import com.kesik.bladecommerce.entity.Admin;
import com.kesik.bladecommerce.repository.AdminRepository;
import com.kesik.bladecommerce.service.AuthService;
import com.kesik.bladecommerce.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // Find admin by username
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: Username not found - {}", request.getUsername());
                    return new RuntimeException("Invalid username or password");
                });

        // Check if admin is enabled
        if (!admin.isEnabled()) {
            log.warn("Login failed: Account disabled - {}", request.getUsername());
            throw new RuntimeException("Account is disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            log.warn("Login failed: Invalid password for username - {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        // Update last login time
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);

        // Generate JWT token
        String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole());

        log.info("Login successful for username: {}", request.getUsername());

        return new LoginResponse(token, admin.getUsername(), admin.getRole());
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
