package com.oleksandr.registerms.repository;


import com.oleksandr.registerms.entity.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, UUID> {
    Mono<RefreshToken> findByToken(String token);
    Mono<Void> deleteByUserId(UUID userId);
    Mono<RefreshToken> findByUserId(UUID userId);
}
