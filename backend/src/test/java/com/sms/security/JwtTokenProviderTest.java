package com.sms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "TestSecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong!";
        long expirationMs = 86400000;
        tokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = tokenProvider.generateToken(1L, "admin@sms.com", "ADMIN");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getEmailFromToken_shouldReturnCorrectEmail() {
        String token = tokenProvider.generateToken(1L, "admin@sms.com", "ADMIN");
        assertEquals("admin@sms.com", tokenProvider.getEmailFromToken(token));
    }

    @Test
    void getRoleFromToken_shouldReturnCorrectRole() {
        String token = tokenProvider.generateToken(1L, "admin@sms.com", "ADMIN");
        assertEquals("ADMIN", tokenProvider.getRoleFromToken(token));
    }

    @Test
    void getRoleFromToken_shouldReturnStudentRole() {
        String token = tokenProvider.generateToken(2L, "student@sms.com", "STUDENT");
        assertEquals("STUDENT", tokenProvider.getRoleFromToken(token));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = tokenProvider.generateToken(1L, "admin@sms.com", "ADMIN");
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_shouldReturnFalseForNullToken() {
        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    void validateToken_shouldReturnFalseForEmptyToken() {
        assertFalse(tokenProvider.validateToken(""));
    }

    @Test
    void validateToken_shouldReturnFalseForTokenSignedWithDifferentKey() {
        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "AnotherSecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong!", 86400000);
        String token = otherProvider.generateToken(1L, "admin@sms.com", "ADMIN");
        assertFalse(tokenProvider.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider(
                "TestSecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong!", 0);
        String token = expiredProvider.generateToken(1L, "admin@sms.com", "ADMIN");
        assertFalse(tokenProvider.validateToken(token));
    }
}
