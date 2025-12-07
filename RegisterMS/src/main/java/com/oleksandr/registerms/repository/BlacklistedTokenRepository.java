package com.oleksandr.registerms.repository;

import com.oleksandr.registerms.entity.BlacklistedToken;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface BlacklistedTokenRepository extends ReactiveCrudRepository<BlacklistedToken, UUID> {

    Mono<BlacklistedToken> findByToken(String token);

    @Query("DELETE FROM blacklisted_tokens WHERE expires_at < :now")
    Mono<Void> deleteExpiredTokens(Instant now);
}
