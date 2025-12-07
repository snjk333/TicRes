package com.oleksandr.eventprovider.TicketMaster;

import com.oleksandr.eventprovider.TicketMaster.dto.TicketmasterResponse;
import com.oleksandr.eventprovider.exception.TicketmasterApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class TicketmasterClient {

    private static final Logger logger = LoggerFactory.getLogger(TicketmasterClient.class);

    private final WebClient webClient;
    private final String apiKey;

    public TicketmasterClient(WebClient.Builder webClientBuilder,
                              @Value("${ticketmaster.api.baseurl}") String baseUrl,
                              @Value("${ticketmaster.api.key}") String apiKey) {

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();

        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .exchangeStrategies(strategies)
                .build();
        this.apiKey = apiKey;
    }

    public Mono<TicketmasterResponse> fetchEvents(String countryCode) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/events.json")
                        .queryParam("apikey", this.apiKey)
                        .queryParam("countryCode", countryCode)
                        .queryParam("size", 50)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        this::handle4xxError
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        this::handle5xxError
                )
                .bodyToMono(TicketmasterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException.ServiceUnavailable
                                || throwable instanceof WebClientResponseException.GatewayTimeout)
                        .doBeforeRetry(retrySignal -> 
                                logger.warn("Retrying request to Ticketmaster API. Attempt: {}", retrySignal.totalRetries() + 1))
                )
                .doOnError(error -> logger.error("Error fetching events from Ticketmaster API: {}", error.getMessage()))
                .onErrorResume(this::handleFallback);
    }

    private Mono<Throwable> handle4xxError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    logger.error("Ticketmaster API 4xx error. Status: {}, Body: {}", response.statusCode(), errorBody);
                    return Mono.error(new TicketmasterApiException(
                            "Client error from Ticketmaster API: " + response.statusCode(),
                            response.statusCode().value(),
                            errorBody
                    ));
                });
    }

    private Mono<Throwable> handle5xxError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    logger.error("Ticketmaster API 5xx error. Status: {}, Body: {}", response.statusCode(), errorBody);
                    return Mono.error(new TicketmasterApiException(
                            "Server error from Ticketmaster API: " + response.statusCode(),
                            response.statusCode().value(),
                            errorBody
                    ));
                });
    }

    private Mono<TicketmasterResponse> handleFallback(Throwable error) {
        logger.error("Fallback triggered for Ticketmaster API. Returning empty response. Error: {}", error.getMessage());

        TicketmasterResponse emptyResponse = new TicketmasterResponse(null);
        return Mono.just(emptyResponse);
    }
}
