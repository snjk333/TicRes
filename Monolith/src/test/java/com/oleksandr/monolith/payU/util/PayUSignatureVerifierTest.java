package com.oleksandr.monolith.payU.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class PayUSignatureVerifierTest {

    private PayUSignatureVerifier payUSignatureVerifier;
    private String testSecondKey;

    @BeforeEach
    void setUp() {
        payUSignatureVerifier = new PayUSignatureVerifier();
        testSecondKey = "test-second-key-123";
        ReflectionTestUtils.setField(payUSignatureVerifier, "payuSecondKey", testSecondKey);
    }

    @Test
    void verifySignature_shouldReturnTrueForValidSignature() {
        // Given
        String requestBody = "{\"order\":{\"orderId\":\"ABC123\"}}";
        String expectedSignature = calculateMD5(requestBody + testSecondKey);

        // When
        boolean result = payUSignatureVerifier.verifySignature(expectedSignature, requestBody);

        // Then
        assertTrue(result);
    }

    @Test
    void verifySignature_shouldReturnFalseForInvalidSignature() {
        // Given
        String requestBody = "{\"order\":{\"orderId\":\"ABC123\"}}";
        String invalidSignature = "invalid-signature-hash";

        // When
        boolean result = payUSignatureVerifier.verifySignature(invalidSignature, requestBody);

        // Then
        assertFalse(result);
    }

    @Test
    void verifySignature_shouldReturnFalseForNullSignature() {
        // Given
        String requestBody = "{\"order\":{\"orderId\":\"ABC123\"}}";

        // When
        boolean result = payUSignatureVerifier.verifySignature(null, requestBody);

        // Then
        assertFalse(result);
    }

    @Test
    void verifySignature_shouldReturnFalseForEmptySignature() {
        // Given
        String requestBody = "{\"order\":{\"orderId\":\"ABC123\"}}";

        // When
        boolean result = payUSignatureVerifier.verifySignature("", requestBody);

        // Then
        assertFalse(result);
    }

    @Test
    void verifySignature_shouldReturnFalseForNullRequestBody() {
        // Given
        String signature = "some-signature";

        // When
        boolean result = payUSignatureVerifier.verifySignature(signature, null);

        // Then
        assertFalse(result);
    }

    @Test
    void verifySignature_shouldReturnFalseForEmptyRequestBody() {
        // Given
        String signature = "some-signature";

        // When
        boolean result = payUSignatureVerifier.verifySignature(signature, "");

        // Then
        assertFalse(result);
    }

    @Test
    void verifySignature_shouldBeCaseInsensitive() {
        // Given
        String requestBody = "{\"order\":{\"orderId\":\"ABC123\"}}";
        String expectedSignature = calculateMD5(requestBody + testSecondKey);
        String uppercaseSignature = expectedSignature.toUpperCase();

        // When
        boolean result = payUSignatureVerifier.verifySignature(uppercaseSignature, requestBody);

        // Then
        assertTrue(result);
    }

    @Test
    void verifySignature_shouldHandleDifferentRequestBodies() {
        // Given
        String requestBody1 = "{\"order\":{\"orderId\":\"ABC123\"}}";
        String requestBody2 = "{\"order\":{\"orderId\":\"XYZ789\"}}";
        String signature1 = calculateMD5(requestBody1 + testSecondKey);

        // When
        boolean result = payUSignatureVerifier.verifySignature(signature1, requestBody2);

        // Then
        assertFalse(result);
    }

    @Test
    void extractSignature_shouldExtractSignatureFromFormattedHeader() {
        // Given
        String headerValue = "signature=abc123def456;algorithm=MD5";

        // When
        String result = payUSignatureVerifier.extractSignature(headerValue);

        // Then
        assertNotNull(result);
        assertEquals("abc123def456", result);
    }

    @Test
    void extractSignature_shouldReturnPlainSignatureWhenNoFormatting() {
        // Given
        String headerValue = "abc123def456";

        // When
        String result = payUSignatureVerifier.extractSignature(headerValue);

        // Then
        assertNotNull(result);
        assertEquals("abc123def456", result);
    }

    @Test
    void extractSignature_shouldReturnNullForNullHeader() {
        // When
        String result = payUSignatureVerifier.extractSignature(null);

        // Then
        assertNull(result);
    }

    @Test
    void extractSignature_shouldReturnNullForEmptyHeader() {
        // When
        String result = payUSignatureVerifier.extractSignature("");

        // Then
        assertNull(result);
    }

    @Test
    void extractSignature_shouldHandleMultipleParameters() {
        // Given
        String headerValue = "algorithm=MD5;signature=abc123def456;timestamp=1234567890";

        // When
        String result = payUSignatureVerifier.extractSignature(headerValue);

        // Then
        assertNotNull(result);
        assertEquals("abc123def456", result);
    }

    @Test
    void extractSignature_shouldTrimWhitespace() {
        // Given
        String headerValue = "signature=abc123def456 ; algorithm=MD5";

        // When
        String result = payUSignatureVerifier.extractSignature(headerValue);

        // Then
        assertNotNull(result);
        assertEquals("abc123def456", result);
    }

    @Test
    void extractSignature_shouldHandleSignatureAtBeginning() {
        // Given
        String headerValue = "signature=abc123def456;algorithm=MD5;other=value";

        // When
        String result = payUSignatureVerifier.extractSignature(headerValue);

        // Then
        assertNotNull(result);
        assertEquals("abc123def456", result);
    }

    @Test
    void extractSignature_shouldReturnHeaderValueWhenNoSignatureParameter() {
        // Given
        String headerValue = "algorithm=MD5;timestamp=1234567890";

        // When
        String result = payUSignatureVerifier.extractSignature(headerValue);

        // Then
        assertNotNull(result);
        assertEquals("algorithm=MD5;timestamp=1234567890", result);
    }

    @Test
    void verifySignature_integrationTest() {
        // Given
        String requestBody = "{\"order\":{\"orderId\":\"TEST123\",\"extOrderId\":\"EXT456\"}}";
        String headerValue = "signature=" + calculateMD5(requestBody + testSecondKey) + ";algorithm=MD5";
        
        // When
        String extractedSignature = payUSignatureVerifier.extractSignature(headerValue);
        boolean isValid = payUSignatureVerifier.verifySignature(extractedSignature, requestBody);

        // Then
        assertTrue(isValid);
    }

    // Helper method to match the private calculateMD5 method in PayUSignatureVerifier
    private String calculateMD5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
