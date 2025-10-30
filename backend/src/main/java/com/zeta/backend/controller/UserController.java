package com.zeta.backend.controller;

import com.zeta.backend.dto.UserResponse;
import com.zeta.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller for user endpoints.
// Handles authenticated user operations (requires valid JWT token).

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//     get authenticated user's profile.
//     GET /user/profile
//     requires: Valid JWT token in Authorization header
//     response: 200 OK with user details
//     authentication -> Spring Security authentication object (contains userId from JWT)
//     return ResponseEntity with UserResponse

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        // extract userId from authentication principal (set by JwtAuthenticationFilter)
        Long userId = (Long) authentication.getPrincipal();
        logger.info("Profile request for userId: {}", userId);

        UserResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}