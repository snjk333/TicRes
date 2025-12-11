package com.oleksandr.monolith.integration.wrapper.reservationIntegration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Slf4j
@Service
public class ReserveService {

    private final WebClient webClient;

    public ReserveService(WebClient.Builder builder,
                          @Value("${event.provider.url}") String eventProviderUrl) {
        this.webClient = builder.baseUrl(eventProviderUrl + "/external").build();
    }

    public void sendBookingCreation(UUID ticketId, UUID id) {
        ReservationRequestDto request = new ReservationRequestDto(ticketId, id);
        log.info("sendBookingCreation");
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/reserveTicket")
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReservationResponseDto.class)
                .block();
    }

    public void sendBookingCancel(UUID ticketId, UUID id) {
        ReservationRequestDto request = new ReservationRequestDto(ticketId, id);
        log.info("sendBookingCancel");
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cancelTicket")
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReservationResponseDto.class)
                .block();
    }
    public void sendBookingComplete(UUID ticketId, UUID id) {
        ReservationRequestDto request = new ReservationRequestDto(ticketId, id);
        log.info("sendBookingComplete");
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/confirmTicket")
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReservationResponseDto.class)
                .block();
    }
}
