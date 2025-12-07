package com.oleksandr.registerms.service;

import com.oleksandr.registerms.entity.BlacklistedToken;
import com.oleksandr.registerms.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistService {

    private final BlacklistedTokenRepository blacklistRepository;

    public Mono<Void> addToBlacklist(String token, UUID userId, long expirationSeconds) {
        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .id(UUID.randomUUID())
                .token(token)
                .userId(userId)
                .blacklistedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expirationSeconds))
                .build();

        return blacklistRepository.save(blacklistedToken)
                .doOnSuccess(saved -> log.info("Token blacklisted for user: {}", userId))
                .then();
    }

    public Mono<Boolean> isBlacklisted(String token) {
        return blacklistRepository.findByToken(token)
                .map(blacklistedToken -> {
                    // Проверить не истёк ли
                    return blacklistedToken.getExpiresAt().isAfter(Instant.now());
                })
                .defaultIfEmpty(false);
    }

    public Mono<Void> cleanupExpiredTokens() {
        return blacklistRepository.deleteExpiredTokens(Instant.now())
                .doOnSuccess(v -> log.info("Expired tokens cleaned up"));
    }
}
