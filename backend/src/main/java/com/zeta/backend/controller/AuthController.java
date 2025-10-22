package com.zeta.backend.controller;

import com.zeta.backend.dto.LoginRequest;
import com.zeta.backend.dto.LoginResponse;
import com.zeta.backend.dto.RegisterRequest;
import com.zeta.backend.dto.UserResponse;
import com.zeta.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller for authentication endpoints.
// Handles user registration and login (unauthenticated endpoints).

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

//     register a new user.
//     POST /auth/register
//     request body: { "name", "email", "password", "phone"? }
//     response: 201 Created with user details (excluding password)
//     request -> registration details
//     return ResponseEntity with UserResponse

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for email: {}", request.getEmail());
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//     authenticate user and return JWT token.
//     POST /auth/login
//     request body: { "email", "password" }
//     response: 200 OK with JWT token
//     request login credentials
//     return ResponseEntity with LoginResponse containing token

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for email: {}", request.getEmail());
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
