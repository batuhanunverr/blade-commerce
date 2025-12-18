package com.kesik.bladecommerce.service;

import com.kesik.bladecommerce.dto.auth.LoginRequest;
import com.kesik.bladecommerce.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    boolean validateToken(String token);
}
