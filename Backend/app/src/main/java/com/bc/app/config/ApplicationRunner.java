package com.bc.app.config;

import com.bc.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationRunner implements CommandLineRunner {

    private final AuthService authService;

    @Override
    public void run(String... args) {
        authService.createInitialAdmin();
    }
} 