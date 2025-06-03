package com.bc.app.service;

import com.bc.app.dto.auth.JwtResponse;
import com.bc.app.dto.auth.LoginRequest;
import com.bc.app.dto.auth.MessageResponse;
import com.bc.app.dto.auth.SignupRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    MessageResponse registerUser(SignupRequest signupRequest);
    MessageResponse validateToken(String token);
    void createInitialAdmin();
} 