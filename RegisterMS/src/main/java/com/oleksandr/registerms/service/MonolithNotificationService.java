package com.oleksandr.registerms.service;

import com.oleksandr.registerms.dto.AddToBlacklistRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonolithNotificationService {

    private final WebClient.Builder webClientBuilder;

    @Value("${monolith.url:http://localhost:8088}")
    private String monolithUrl;

    public Mono<Void> notifyBlacklist(String token) {
        return webClientBuilder.build()
                .post()
                .uri(monolithUrl + "/api/internal/blacklist/add")
                .bodyValue(new AddToBlacklistRequest(token))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Monolith notified about blacklisted token"))
                .doOnError(ex -> log.error("Failed to notify Monolith: {}", ex.getMessage()))
                .onErrorResume(throwable -> Mono.empty()); // Не падать если Monolith недоступен
    }
}
