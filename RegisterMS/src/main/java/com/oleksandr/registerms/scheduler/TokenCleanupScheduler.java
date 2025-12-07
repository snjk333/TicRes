package com.oleksandr.registerms.scheduler;

import com.oleksandr.registerms.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final BlacklistService blacklistService;

    @Scheduled(fixedDelayString = "${scheduler.cleanup-delay:3600000}") // 1 hour
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired blacklisted tokens");
        blacklistService.cleanupExpiredTokens()
                .subscribe(
                        v -> log.info("Successfully cleaned up expired tokens"),
                        error -> log.error("Error during token cleanup: {}", error.getMessage())
                );
    }
}
