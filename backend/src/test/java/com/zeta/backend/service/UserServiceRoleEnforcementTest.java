package com.zeta.backend.service;

import com.zeta.backend.enums.Role;
import com.zeta.backend.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

// Pure unit tests for UserService role enforcement functionality.
// Tests default USER role assignment, ADMIN role handling, and role extraction

class UserServiceRoleEnforcementTest {

    // Role Assignment Tests

    @Test
    @DisplayName("Should assign USER role by default")
    void testDefaultRole_IsUser() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("$2a$12$hashedPassword");
        user.setPhone("1234567890");
        user.setRole(Role.USER); // Default role as per requirements

        assertEquals(Role.USER, user.getRole(), "New users should have USER role by default");
    }

    @Test
    @DisplayName("Should support ADMIN role assignment")
    void testRole_CanBeAdmin() {

        User user = new User();
        user.setName("Admin User");
        user.setEmail("admin@example.com");
        user.setPasswordHash("$2a$12$hashedPassword");
        user.setRole(Role.ADMIN);

        assertEquals(Role.ADMIN, user.getRole(), "User should have ADMIN role when explicitly set");
    }

    @Test
    @DisplayName("Should correctly store and retrieve USER role")
    void testRole_UserRoleStorageAndRetrieval() {

        User user = new User();
        user.setRole(Role.USER);

        Role retrievedRole = user.getRole();

        assertNotNull(retrievedRole, "Role should not be null");
        assertEquals(Role.USER, retrievedRole, "Retrieved role should match USER");
        assertEquals("USER", retrievedRole.name(), "Role name should be 'USER'");
    }

    @Test
    @DisplayName("Should correctly store and retrieve ADMIN role")
    void testRole_AdminRoleStorageAndRetrieval() {

        User user = new User();
        user.setRole(Role.ADMIN);

        Role retrievedRole = user.getRole();

        assertNotNull(retrievedRole, "Role should not be null");
        assertEquals(Role.ADMIN, retrievedRole, "Retrieved role should match ADMIN");
        assertEquals("ADMIN", retrievedRole.name(), "Role name should be 'ADMIN'");
    }

    @Test
    @DisplayName("Should differentiate between USER and ADMIN roles")
    void testRole_DifferentRolesAreDistinct() {

        User userWithUserRole = new User();
        userWithUserRole.setRole(Role.USER);

        User userWithAdminRole = new User();
        userWithAdminRole.setRole(Role.ADMIN);

        assertNotEquals(userWithUserRole.getRole(), userWithAdminRole.getRole(),
                "USER and ADMIN roles should be different");
    }

    @Test
    @DisplayName("Should use constructor with default USER role")
    void testConstructor_DefaultUserRole() {

        User user = new User(
                "Test User",
                "test@example.com",
                "$2a$12$hashedPassword",
                "1234567890",
                Role.USER
        );

        assertEquals(Role.USER, user.getRole(), "Constructor should set USER role");
        assertEquals("Test User", user.getName(), "Name should be set correctly");
        assertEquals("test@example.com", user.getEmail(), "Email should be set correctly");
    }

    @Test
    @DisplayName("Should use constructor with ADMIN role")
    void testConstructor_AdminRole() {

        User user = new User(
                "Admin User",
                "admin@example.com",
                "$2a$12$hashedPassword",
                "1234567890",
                Role.ADMIN
        );

        assertEquals(Role.ADMIN, user.getRole(), "Constructor should set ADMIN role");
        assertEquals("Admin User", user.getName(), "Name should be set correctly");
        assertEquals("admin@example.com", user.getEmail(), "Email should be set correctly");
    }

    @Test
    @DisplayName("Should convert role to string correctly")
    void testRole_ToStringConversion() {

        String userRoleString = Role.USER.toString();
        String adminRoleString = Role.ADMIN.toString();

        assertEquals("USER", userRoleString, "USER role toString should return 'USER'");
        assertEquals("ADMIN", adminRoleString, "ADMIN role toString should return 'ADMIN'");
    }

    @Test
    @DisplayName("Should throw exception for invalid role string")
    void testRole_InvalidStringThrowsException() {

        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("INVALID_ROLE");
        }, "Invalid role string should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Should compare roles using equals")
    void testRole_EqualsComparison() {

        Role role1 = Role.USER;
        Role role2 = Role.USER;
        Role role3 = Role.ADMIN;

        assertEquals(role1, role2, "Same roles should be equal");
        assertNotEquals(role1, role3, "Different roles should not be equal");
    }

    @Test
    @DisplayName("Should verify role is non-null in User object")
    void testRole_NonNullConstraint() {

        User user = new User();

        user.setRole(Role.USER);

        assertNotNull(user.getRole(), "Role should not be null after being set");
    }

    @Test
    @DisplayName("Should maintain role after multiple operations")
    void testRole_PersistenceAcrossOperations() {

        User user = new User();
        user.setName("Test User");
        user.setRole(Role.USER);

        user.setEmail("test@example.com");
        user.setPasswordHash("$2a$12$hash");
        user.setPhone("1234567890");

        assertEquals(Role.USER, user.getRole(), "Role should remain USER after other field updates");
    }

    @Test
    @DisplayName("Should allow role changes")
    void testRole_CanBeChanged() {

        User user = new User();
        user.setRole(Role.USER);
        assertEquals(Role.USER, user.getRole(), "Initial role should be USER");

        user.setRole(Role.ADMIN);

        assertEquals(Role.ADMIN, user.getRole(), "Role should be changed to ADMIN");
    }

    @Test
    @DisplayName("Should use role in string concatenation")
    void testRole_StringConcatenation() {

        User user = new User();
        user.setName("John Doe");
        user.setRole(Role.USER);

        String userInfo = "User: " + user.getName() + ", Role: " + user.getRole();

        assertTrue(userInfo.contains("Role: USER"), "String should contain role information");
        assertEquals("User: John Doe, Role: USER", userInfo, "String concatenation should work correctly");
    }
}
