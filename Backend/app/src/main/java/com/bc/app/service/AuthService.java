package com.bc.app.service;

import com.bc.app.dto.LoginRequest;
import com.bc.app.dto.RegisterRequest;
import com.bc.app.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
    void createInitialAdmin();
} 