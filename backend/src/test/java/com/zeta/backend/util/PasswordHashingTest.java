package com.zeta.backend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

// unit tests for password hashing functionality.
// tests bcrypt hashing, cost factor 12, password matching, and salt randomness without Spring context.

class PasswordHashingTest {

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // BCryptPasswordEncoder with cost factor 12 (as per requirements)
        passwordEncoder = new BCryptPasswordEncoder(12);
    }

// Password Hashing Tests

    @Test
    @DisplayName("Should hash password (not store plain text)")
    void testPasswordHashing_NotPlainText() {

        String plainPassword = "plainTextPassword123";

        String hashedPassword = passwordEncoder.encode(plainPassword);


        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertNotEquals(plainPassword, hashedPassword,
                "Password should be hashed, not stored as plain text");
        assertTrue(hashedPassword.startsWith("$2"),
                "Password hash should start with bcrypt identifier ($2a$ or $2b$)");
    }

    @Test
    @DisplayName("Should use bcrypt cost factor 12")
    void testPasswordHashing_CostFactor12() {

        String password = "password123";

        String hashedPassword = passwordEncoder.encode(password);

        // Bcrypt hash format: $2a$12$... or $2b$12$... where 12 is the cost factor
        assertTrue(hashedPassword.startsWith("$2a$12$") || hashedPassword.startsWith("$2b$12$"),
                "Password hash should use bcrypt cost factor 12 (format: $2a$12$ or $2b$12$)");
    }

    @Test
    @DisplayName("Should verify bcrypt cost factor 12 from BCryptPasswordEncoder")
    void testPasswordEncoder_CostFactor12Verification() throws Exception {

        Field strengthField = BCryptPasswordEncoder.class.getDeclaredField("strength");
        strengthField.setAccessible(true);
        int costFactor = (int) strengthField.get(passwordEncoder);

        assertEquals(12, costFactor, "BCryptPasswordEncoder should be configured with cost factor 12");
    }

    @Test
    @DisplayName("Should successfully verify correct password")
    void testPasswordMatching_CorrectPassword() {

        String password = "correctPassword123";
        String hashedPassword = passwordEncoder.encode(password);

        boolean matches = passwordEncoder.matches(password, hashedPassword);

        assertTrue(matches, "Correct password should match its hash");
    }

    @Test
    @DisplayName("Should reject wrong password")
    void testPasswordMatching_WrongPassword() {

        String correctPassword = "correctPassword123";
        String wrongPassword = "wrongPassword456";
        String hashedPassword = passwordEncoder.encode(correctPassword);

        boolean matches = passwordEncoder.matches(wrongPassword, hashedPassword);

        assertFalse(matches, "Wrong password should not match the hash");
    }

    @Test
    @DisplayName("Should verify password matching works correctly with bcrypt")
    void testPasswordMatching_BcryptVerification() {

        String originalPassword = "mySecurePassword789";
        String hashedPassword = passwordEncoder.encode(originalPassword);

        boolean matchesCorrect = passwordEncoder.matches(originalPassword, hashedPassword);
        boolean matchesWrong = passwordEncoder.matches("wrongPassword", hashedPassword);

        assertTrue(matchesCorrect, "Original password should match its hash");
        assertFalse(matchesWrong, "Wrong password should not match the hash");
    }

    @Test
    @DisplayName("Should handle empty password")
    void testPasswordHashing_EmptyPassword() {

        String emptyPassword = "";

        String hashedPassword = passwordEncoder.encode(emptyPassword);

        assertNotNull(hashedPassword, "Even empty password should be hashed");
        assertNotEquals("", hashedPassword, "Empty password should produce non-empty hash");
        assertTrue(hashedPassword.startsWith("$2a$12$") || hashedPassword.startsWith("$2b$12$"),
                "Empty password hash should still use bcrypt cost factor 12");

        // Verify empty password can be matched
        assertTrue(passwordEncoder.matches("", hashedPassword), "Empty password should be verifiable");
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void testPasswordHashing_SpecialCharactersPassword() {

        String specialPassword = "P@ssw0rd!#$%^&*()_+{}[]|:;<>?,./~`";

        String hashedPassword = passwordEncoder.encode(specialPassword);

        assertNotNull(hashedPassword, "Password with special characters should be hashed");
        assertTrue(passwordEncoder.matches(specialPassword, hashedPassword),
                "Special character password should be verifiable");
    }

    @Test
    @DisplayName("Should reject empty password when actual password is hashed")
    void testPasswordMatching_EmptyPasswordRejected() {

        String actualPassword = "realPassword123";
        String hashedPassword = passwordEncoder.encode(actualPassword);


        boolean matches = passwordEncoder.matches("", hashedPassword);

        assertFalse(matches, "Empty password should not match non-empty password hash");
    }

    @Test
    @DisplayName("Should reject password with slight variations")
    void testPasswordMatching_SlightVariations() {

        String password = "Password123";
        String hashedPassword = passwordEncoder.encode(password);

        assertFalse(passwordEncoder.matches("password123", hashedPassword), "Lowercase P should not match");
        assertFalse(passwordEncoder.matches("Password124", hashedPassword), "Different digit should not match");
        assertFalse(passwordEncoder.matches("Password123 ", hashedPassword), "Extra space should not match");
        assertFalse(passwordEncoder.matches(" Password123", hashedPassword), "Leading space should not match");
        assertFalse(passwordEncoder.matches("Password12", hashedPassword), "Missing character should not match");
    }

    @Test
    @DisplayName("Should verify hashes are always 60 characters long")
    void testPasswordHashing_HashLength() {

        String shortPassword = "abc";
        String longPassword = "a".repeat(70); // 70 chars (under 72-byte BCrypt limit)

        String shortHash = passwordEncoder.encode(shortPassword);
        String longHash = passwordEncoder.encode(longPassword);

        assertEquals(60, shortHash.length(), "Bcrypt hash should always be 60 characters");
        assertEquals(60, longHash.length(), "Bcrypt hash should always be 60 characters regardless of password length");
    }

    @Test
    @DisplayName("Should reject null password in matching")
    void testPasswordMatching_NullPassword() {

        String password = "testPassword";
        String hashedPassword = passwordEncoder.encode(password);

        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.matches(null, hashedPassword);
        }, "Null password in matching should throw IllegalArgumentException");
    }
}
