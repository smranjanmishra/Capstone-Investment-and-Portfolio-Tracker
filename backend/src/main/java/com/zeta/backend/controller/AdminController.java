package com.zeta.backend.controller;

import com.zeta.backend.dto.UserResponse;
import com.zeta.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// controller for admin endpoints.
// handles admin-only operations (requires ADMIN role).

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

//     get all users (admin only).
//     GET /admin/users
//     requires: valid JWT token with ADMIN role
//     response: 200 OK with list of all users (excluding passwords)
//     return ResponseEntity with List of UserResponse

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Admin request to get all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}