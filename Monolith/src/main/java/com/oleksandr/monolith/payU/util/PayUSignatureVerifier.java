package com.oleksandr.monolith.payU.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Slf4j
@Service
public class PayUSignatureVerifier {

    @Value("${payu.second.key}")
    private String payuSecondKey;

    public boolean verifySignature(String receivedSignature, String requestBody) {
        if (receivedSignature == null || receivedSignature.isEmpty()) {
            log.error("❌ Missing OpenPayu-Signature header");
            return false;
        }

        if (requestBody == null || requestBody.isEmpty()) {
            log.error("❌ Empty request body for signature verification");
            return false;
        }

        try {
            String expectedSignature = calculateMD5(requestBody + payuSecondKey);

            log.debug("🔐 Received signature: {}", receivedSignature);
            log.debug("🔐 Expected signature: {}", expectedSignature);
            log.debug("🔐 Request body length: {} bytes", requestBody.length());

            boolean isValid = expectedSignature.equalsIgnoreCase(receivedSignature);

            if (isValid) {
                log.info("✅ PayU signature verification PASSED");
            } else {
                log.error("❌ PayU signature verification FAILED!");
                log.error("❌ Expected: {}", expectedSignature);
                log.error("❌ Received: {}", receivedSignature);
            }

            return isValid;

        } catch (Exception e) {
            log.error("❌ Error during signature verification", e);
            return false;
        }
    }

    private String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("❌ MD5 algorithm not available", e);
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    public String extractSignature(String headerValue) {
        if (headerValue == null || headerValue.isEmpty()) {
            return null;
        }
        if (headerValue.contains("signature=")) {
            String[] parts = headerValue.split(";");
            for (String part : parts) {
                if (part.trim().startsWith("signature=")) {
                    return part.trim().substring("signature=".length());
                }
            }
        }
        return headerValue.trim();
    }
}
