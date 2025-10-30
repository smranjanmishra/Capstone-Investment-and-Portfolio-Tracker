package com.zeta.backend.util;

import com.zeta.backend.enums.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

//Tests token generation, validation, and claim extraction

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private final Long testExpiration = 1800000L; // 30 minutes

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // Set private fields using reflection for pure unit testing
        setField(jwtUtil, "secret", testSecret);
        setField(jwtUtil, "expiration", testExpiration);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // Token Generation Tests

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken_Success() {

        Long userId = 1L;
        Role role = Role.USER;

        String token = jwtUtil.generateToken(userId, role);

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertEquals(3, token.split("\\.").length, "Token should have 3 parts (header.payload.signature)");
    }

    @Test
    @DisplayName("Should generate different tokens for same user (due to issued time)")
    void testGenerateToken_DifferentTokensForSameUser() throws InterruptedException {

        Long userId = 1L;
        Role role = Role.USER;

        String token1 = jwtUtil.generateToken(userId, role);
        Thread.sleep(1100); // 1.1 second delay to ensure different issuedAt time (JWT uses seconds
                            // precision)
        String token2 = jwtUtil.generateToken(userId, role);

        assertNotEquals(token1, token2, "Tokens should be different due to different issued times");
    }

    @Test
    @DisplayName("Should generate token with correct userId claim")
    void testGenerateToken_ContainsCorrectUserId() {

        Long userId = 123L;
        Role role = Role.USER;

        String token = jwtUtil.generateToken(userId, role);
        Long extractedUserId = jwtUtil.extractUserId(token);

        assertEquals(userId, extractedUserId, "Extracted userId should match original");
    }

    @Test
    @DisplayName("Should generate token with correct role claim")
    void testGenerateToken_ContainsCorrectRole() {

        Long userId = 1L;
        Role role = Role.ADMIN;

        String token = jwtUtil.generateToken(userId, role);
        Role extractedRole = jwtUtil.extractRole(token);

        assertEquals(role, extractedRole, "Extracted role should match original");
    }

    @Test
    @DisplayName("Should generate token for USER role")
    void testGenerateToken_UserRole() {

        Long userId = 1L;
        Role role = Role.USER;

        String token = jwtUtil.generateToken(userId, role);
        Role extractedRole = jwtUtil.extractRole(token);

        assertEquals(Role.USER, extractedRole, "Role should be USER");
    }

    @Test
    @DisplayName("Should generate token for ADMIN role")
    void testGenerateToken_AdminRole() {

        Long userId = 2L;
        Role role = Role.ADMIN;

        String token = jwtUtil.generateToken(userId, role);
        Role extractedRole = jwtUtil.extractRole(token);

        assertEquals(Role.ADMIN, extractedRole, "Role should be ADMIN");
    }

    // Token Validation Tests

    @Test
    @DisplayName("Should validate correct token successfully")
    void testValidateToken_ValidToken() {

        Long userId = 1L;
        Role role = Role.USER;
        String token = jwtUtil.generateToken(userId, role);

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid, "Valid token should pass validation");
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testValidateToken_InvalidToken() {

        String invalidToken = "invalid.token.here";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid, "Invalid token should fail validation");
    }

    @Test
    @DisplayName("Should reject malformed token")
    void testValidateToken_MalformedToken() {

        String malformedToken = "not-a-jwt-token";

        boolean isValid = jwtUtil.validateToken(malformedToken);

        assertFalse(isValid, "Malformed token should fail validation");
    }

    @Test
    @DisplayName("Should reject empty token")
    void testValidateToken_EmptyToken() {

        String emptyToken = "";

        boolean isValid = jwtUtil.validateToken(emptyToken);

        assertFalse(isValid, "Empty token should fail validation");
    }

    @Test
    @DisplayName("Should reject null token")
    void testValidateToken_NullToken() {

        String nullToken = null;

        boolean isValid = jwtUtil.validateToken(nullToken);

        assertFalse(isValid, "Null token should return false");
    }

    @Test
    @DisplayName("Should reject token with wrong signature")
    void testValidateToken_WrongSignature() {
        // create token with different secret
        JwtUtil differentSecretUtil = new JwtUtil();
        try {
            setField(differentSecretUtil, "secret", "differentSecretKey12345678901234567890123456789012");
            setField(differentSecretUtil, "expiration", testExpiration);
        } catch (Exception e) {
            fail("Failed to setup test: " + e.getMessage());
        }

        String tokenWithWrongSecret = differentSecretUtil.generateToken(1L, Role.USER);

        boolean isValid = jwtUtil.validateToken(tokenWithWrongSecret);

        assertFalse(isValid, "Token signed with different secret should fail validation");
    }

    // Token Expiry Tests

    @Test
    @DisplayName("Should not be expired immediately after generation")
    void testIsTokenExpired_NewToken() {

        Long userId = 1L;
        Role role = Role.USER;
        String token = jwtUtil.generateToken(userId, role);

        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired, "Newly generated token should not be expired");
    }

    @Test
    @DisplayName("Should detect expired token")
    void testIsTokenExpired_ExpiredToken() throws Exception {
        // Create util with very short expiration (1 millisecond)
        JwtUtil shortExpiryUtil = new JwtUtil();
        setField(shortExpiryUtil, "secret", testSecret);
        setField(shortExpiryUtil, "expiration", 1L); // 1 millisecond

        String token = shortExpiryUtil.generateToken(1L, Role.USER);

        // Wait for token to expire
        Thread.sleep(10);

        boolean isExpired = shortExpiryUtil.isTokenExpired(token);

        assertTrue(isExpired, "Token should be expired after expiration time");
    }

    @Test
    @DisplayName("Should reject validation of expired token")
    void testValidateToken_ExpiredToken() throws Exception {
        // Create util with very short expiration
        JwtUtil shortExpiryUtil = new JwtUtil();
        setField(shortExpiryUtil, "secret", testSecret);
        setField(shortExpiryUtil, "expiration", 1L); // 1 millisecond

        String token = shortExpiryUtil.generateToken(1L, Role.USER);

        // Wait for token to expire
        Thread.sleep(10);

        boolean isValid = shortExpiryUtil.validateToken(token);

        assertFalse(isValid, "Expired token should fail validation");
    }

    // Claim Extraction Tests

    @Test
    @DisplayName("Should extract userId claim correctly")
    void testExtractUserId_Success() {

        Long expectedUserId = 999L;
        Role role = Role.USER;
        String token = jwtUtil.generateToken(expectedUserId, role);

        Long actualUserId = jwtUtil.extractUserId(token);

        assertEquals(expectedUserId, actualUserId, "Extracted userId should match original");
    }

    @Test
    @DisplayName("Should extract role claim correctly")
    void testExtractRole_Success() {

        Long userId = 1L;
        Role expectedRole = Role.ADMIN;
        String token = jwtUtil.generateToken(userId, expectedRole);

        Role actualRole = jwtUtil.extractRole(token);

        assertEquals(expectedRole, actualRole, "Extracted role should match original");
    }

    @Test
    @DisplayName("Should extract all claims correctly")
    void testExtractClaims_Success() {

        Long userId = 42L;
        Role role = Role.USER;
        String token = jwtUtil.generateToken(userId, role);

        Claims claims = jwtUtil.extractClaims(token);

        assertNotNull(claims, "Claims should not be null");
        assertEquals(userId, claims.get("userId", Long.class), "userId claim should match");
        assertEquals(role.name(), claims.get("role", String.class), "role claim should match");
        assertNotNull(claims.getIssuedAt(), "issuedAt should be present");
        assertNotNull(claims.getExpiration(), "expiration should be present");
    }

    @Test
    @DisplayName("Should not contain jti claim (as per requirements)")
    void testToken_DoesNotContainJtiClaim() {

        Long userId = 1L;
        Role role = Role.USER;
        String token = jwtUtil.generateToken(userId, role);

        Claims claims = jwtUtil.extractClaims(token);

        assertNull(claims.get("jti"), "Token should NOT contain jti claim");
        assertNull(claims.getId(), "Token ID (jti) should be null");
    }

    @Test
    @DisplayName("Should verify token expiration is 30 minutes")
    void testToken_ExpirationIs30Minutes() {

        Long userId = 1L;
        Role role = Role.USER;

        long beforeGeneration = System.currentTimeMillis();
        String token = jwtUtil.generateToken(userId, role);
        long afterGeneration = System.currentTimeMillis();

        Claims claims = jwtUtil.extractClaims(token);
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        long tokenLifetime = expiration.getTime() - issuedAt.getTime();
        assertEquals(testExpiration, tokenLifetime, "Token lifetime should be exactly 30 minutes (1800000ms)");

        // Verify expiration is approximately 30 minutes from now
        long expectedExpiration = (beforeGeneration + afterGeneration) / 2 + testExpiration;
        long actualExpiration = expiration.getTime();
        long tolerance = 1000; // 1 second tolerance
        assertTrue(Math.abs(actualExpiration - expectedExpiration) < tolerance,
                "Expiration should be approximately 30 minutes from issuance");
    }
}
