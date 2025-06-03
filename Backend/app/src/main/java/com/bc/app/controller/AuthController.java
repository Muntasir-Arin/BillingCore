package com.bc.app.controller;

import com.bc.app.dto.auth.JwtResponse;
import com.bc.app.dto.auth.LoginRequest;
import com.bc.app.dto.auth.MessageResponse;
import com.bc.app.dto.auth.SignupRequest;
import com.bc.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.registerUser(signupRequest));
    }

    @PostMapping("/validate")
    public ResponseEntity<MessageResponse> validateToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.validateToken(token.substring(7)));
    }
} 