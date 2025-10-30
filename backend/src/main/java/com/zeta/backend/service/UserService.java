package com.zeta.backend.service;

import com.zeta.backend.dto.LoginRequest;
import com.zeta.backend.dto.LoginResponse;
import com.zeta.backend.dto.RegisterRequest;
import com.zeta.backend.dto.UserResponse;
import com.zeta.backend.enums.Role;
import com.zeta.backend.exceptions.DuplicateEmailException;
import com.zeta.backend.exceptions.InvalidCredentialsException;
import com.zeta.backend.exceptions.UserNotFoundException;
import com.zeta.backend.models.User;
import com.zeta.backend.repository.UserRepository;
import com.zeta.backend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Service layer for user-related business logic.
// Handles user registration, authentication, and profile management.

@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
//        BCrypt with strength 12
        passwordEncoder = new BCryptPasswordEncoder(12);
    }

//     register a new user with role USER by default.
//     validates email uniqueness and hashes password using bcrypt with cost factor 12.
//     request -> registration details
//     return UserResponse with created user details (excluding password)
//     throws DuplicateEmailException if email already exists

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: email already exists - {}", request.getEmail());
            throw new DuplicateEmailException("Email already exists");
        }

        // Hash password using bcrypt with cost factor 12
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // create new user with USER role by default
        User user = new User(
                request.getName(),
                request.getEmail(),
                hashedPassword,
                request.getPhone(),
                Role.USER
        );

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        return UserResponse.fromUser(savedUser);
    }

//     authenticate user and generate JWT token.
//     verifies credentials using bcrypt password matching.
//     request -> login credentials
//     return LoginResponse containing JWT token
//     throws InvalidCredentialsException if credentials are invalid

    public LoginResponse loginUser(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: user not found - {}", request.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        // Verify password using bcrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            logger.warn("Login failed: invalid password for email - {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token with userId and role claims
        String token = jwtUtil.generateToken(Math.toIntExact(user.getId()), user.getRole());
        logger.info("Login successful for userId: {}, role: {}", user.getId(), user.getRole());

        return new LoginResponse(token);
    }

//     get user profile by user ID.
//     userId -> the user's ID
//     return UserResponse with user details (excluding password)
//     throws UserNotFoundException if user not found

    public UserResponse getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found: userId={}", userId);
                    return new UserNotFoundException("User not found");
                });

        return UserResponse.fromUser(user);
    }

//     get all users (admin only).
//     returns all users excluding password hashes.
//     return List of UserResponse objects

    public List<UserResponse> getAllUsers() {
        logger.info("Fetching all users for admin");
        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    // Retrieve a user by their unique ID.
    // id -> ID of the user to fetch
    // return -> Optional<User> containing the user if found

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}