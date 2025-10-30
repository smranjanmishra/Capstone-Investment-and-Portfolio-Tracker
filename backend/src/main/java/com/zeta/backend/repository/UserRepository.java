package com.zeta.backend.repository;

import com.zeta.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository interface for User entity.
// Provides CRUD operations and custom queries for users.

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Find user by email.
//    Used for authentication and checking duplicate emails.
//    email -> the email to search for
//    return Optional containing the user if found

    Optional<User> findByEmail(String email);

//     Check if a user exists with the given email.
//     Used to validate email uniqueness during registration.
//     email -> the email to check
//     return true if email exists, false otherwise

    boolean existsByEmail(String Email);

    //method to find user by user id
    Optional<User>findById(Long userId);
}
