package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.auth.LoginRequest;
import com.kesik.bladecommerce.dto.auth.LoginResponse;
import com.kesik.bladecommerce.dto.common.ApiResponse;
import com.kesik.bladecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        ApiResponse<LoginResponse> response = ApiResponse.success("Login successful", loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            boolean isValid = authService.validateToken(token);

            ApiResponse<Map<String, Boolean>> response = ApiResponse.success(
                    Map.of("valid", isValid)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Boolean>> response = ApiResponse.success(
                    Map.of("valid", false)
            );
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        ApiResponse<Map<String, String>> response = ApiResponse.success(
                "Auth service is running",
                Map.of("status", "ok")
        );
        return ResponseEntity.ok(response);
    }
}
