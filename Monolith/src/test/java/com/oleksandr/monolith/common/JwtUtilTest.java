package com.oleksandr.monolith.common;

import com.oleksandr.monolith.blacklist.BlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private BlacklistService blacklistService;

    @InjectMocks
    private JwtUtil jwtUtil;

    private String jwtSecret;
    private SecretKey secretKey;
    private UUID testUserId;
    private String testUsername;

    @BeforeEach
    void setUp() {
        jwtSecret = "MyVerySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345";
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        testUserId = UUID.randomUUID();
        testUsername = "testuser";

        // Inject the secret using reflection
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", jwtSecret);
    }

    @Test
    void extractUserId_shouldExtractUserIdFromToken() {
        // Given
        String token = createTestToken(testUserId.toString(), testUsername);

        // When
        UUID result = jwtUtil.extractUserId(token);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result);
    }

    @Test
    void extractUserId_shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.extractUserId(invalidToken));
    }

    @Test
    void extractUsername_shouldExtractUsernameFromToken() {
        // Given
        String token = createTestToken(testUserId.toString(), testUsername);

        // When
        String result = jwtUtil.extractUsername(token);

        // Then
        assertNotNull(result);
        assertEquals(testUsername, result);
    }

    @Test
    void extractUsername_shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        // Given
        String token = createTestToken(testUserId.toString(), testUsername);
        when(blacklistService.isBlacklisted(token)).thenReturn(false);

        // When
        boolean result = jwtUtil.isTokenValid(token);

        // Then
        assertTrue(result);
        verify(blacklistService, times(1)).isBlacklisted(token);
    }

    @Test
    void isTokenValid_shouldReturnFalseForBlacklistedToken() {
        // Given
        String token = createTestToken(testUserId.toString(), testUsername);
        when(blacklistService.isBlacklisted(token)).thenReturn(true);

        // When
        boolean result = jwtUtil.isTokenValid(token);

        // Then
        assertFalse(result);
        verify(blacklistService, times(1)).isBlacklisted(token);
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";
        when(blacklistService.isBlacklisted(invalidToken)).thenReturn(false);

        // When
        boolean result = jwtUtil.isTokenValid(invalidToken);

        // Then
        assertFalse(result);
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        // Given
        String expiredToken = createExpiredToken(testUserId.toString(), testUsername);
        when(blacklistService.isBlacklisted(expiredToken)).thenReturn(false);

        // When
        boolean result = jwtUtil.isTokenValid(expiredToken);

        // Then
        assertFalse(result);
    }

    @Test
    void extractTokenFromHeader_shouldExtractTokenFromBearerHeader() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        String authHeader = "Bearer " + token;

        // When
        String result = jwtUtil.extractTokenFromHeader(authHeader);

        // Then
        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    void extractTokenFromHeader_shouldReturnNullForNullHeader() {
        // When
        String result = jwtUtil.extractTokenFromHeader(null);

        // Then
        assertNull(result);
    }

    @Test
    void extractTokenFromHeader_shouldReturnNullForHeaderWithoutBearer() {
        // Given
        String authHeader = "Basic some-credentials";

        // When
        String result = jwtUtil.extractTokenFromHeader(authHeader);

        // Then
        assertNull(result);
    }

    @Test
    void extractTokenFromHeader_shouldReturnNullForEmptyHeader() {
        // Given
        String authHeader = "";

        // When
        String result = jwtUtil.extractTokenFromHeader(authHeader);

        // Then
        assertNull(result);
    }

    @Test
    void extractTokenFromHeader_shouldHandleBearerWithoutSpace() {
        // Given
        String authHeader = "Bearer";

        // When
        String result = jwtUtil.extractTokenFromHeader(authHeader);

        // Then
        assertNull(result);
    }

    @Test
    void extractTokenFromHeader_shouldHandleBearerWithExtraSpaces() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        String authHeader = "Bearer  " + token; // Extra space

        // When
        String result = jwtUtil.extractTokenFromHeader(authHeader);

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith(" ")); // Should include the extra space
    }

    // Helper methods

    private String createTestToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(secretKey)
                .compact();
    }

    private String createExpiredToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .signWith(secretKey)
                .compact();
    }
}
