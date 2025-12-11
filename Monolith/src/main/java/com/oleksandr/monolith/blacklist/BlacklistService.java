package com.oleksandr.monolith.blacklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistService {

    private final BlacklistedTokenRepository blacklistRepository;

    @Transactional
    public void addToBlacklist(String token) {
        if (blacklistRepository.findByToken(token).isPresent()) {
            log.warn("Token already in blacklist: {}", token.substring(0, 20));
            return;
        }

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .id(UUID.randomUUID())
                .token(token)
                .userId(null)
                .blacklistedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600)) // 1 hour
                .build();

        blacklistRepository.save(blacklistedToken);
        log.info("Token added to blacklist");
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepository.findByToken(token)
                .map(blacklistedToken -> {
                    boolean isValid = blacklistedToken.getExpiresAt().isAfter(Instant.now());
                    if (!isValid) {
                        log.debug("Token in blacklist but expired");
                    }
                    return isValid;
                })
                .orElse(false);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        blacklistRepository.deleteExpiredTokens(Instant.now());
        log.info("Expired tokens cleaned up");
    }
}
