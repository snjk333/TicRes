package com.oleksandr.eventprovider.TicketMaster;

import com.oleksandr.eventprovider.Ticket.Ticket;
import com.oleksandr.eventprovider.Ticket.TicketRepository;
import com.oleksandr.eventprovider.TicketMaster.dto.ReserveTicketSimulation.ReservationRequestDto;
import com.oleksandr.eventprovider.TicketMaster.dto.ReserveTicketSimulation.ReservationResponseDto;
import com.oleksandr.eventprovider.exception.TicketNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Slf4j
@Service
public class EventReservationExternalService {

    private final TicketRepository ticketRepository;

    @Value("${reservation.api.baseurl}")
    private String ExternalServiceUrl;

    private final WebClient webClient;

    public EventReservationExternalService(TicketRepository ticketRepository, WebClient.Builder builder) {
        this.ticketRepository = ticketRepository;
        this.webClient = builder.baseUrl(ExternalServiceUrl).build();
    }

    private String extractEventId(UUID ticketId) {
        // Пытаемся найти билет в локальной базе EventProvider
        return ticketRepository.findById(ticketId)
                .map(ticket -> ticket.getEvent().getExternalId())
                // Если билет не найден - возвращаем dummy ID (псевдо-резервация для промоутера)
                .orElse("EXTERNAL_EVENT_UNKNOWN");
    }

    public ReservationResponseDto createExternalReservation(@Valid ReservationRequestDto requestDto) {
        String externalEventID = this.extractEventId(requestDto.ticketId());
        
        // Симуляция запроса к внешнему API (для демонстрации промоутеру)
        // В реальности Monolith управляет своими билетами в своей базе
        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/createReservation")
                            .queryParam("externalEventID", externalEventID)
                            .build())
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(ReservationResponseDto.class)
                    .block();
        } catch (Exception e) {
            // Игнорируем ошибки внешнего API - это только симуляция
            log.warn("External reservation call failed (expected for simulation): {}", e.getMessage());
        }

        return new ReservationResponseDto(requestDto.ticketId());
    }

    public ReservationResponseDto cancelExternalReservation(@Valid ReservationRequestDto requestDto) {
        String externalEventID = this.extractEventId(requestDto.ticketId());

        // Симуляция отмены резервации во внешнем API
        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/cancelReservation")
                            .queryParam("externalEventID", externalEventID)
                            .build())
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(ReservationResponseDto.class)
                    .block();
        } catch (Exception e) {
            log.warn("External cancellation call failed (expected for simulation): {}", e.getMessage());
        }

        return new ReservationResponseDto(requestDto.ticketId());
    }

    public ReservationResponseDto confirmExternalReservation(@Valid ReservationRequestDto requestDto) {
        String externalEventID = this.extractEventId(requestDto.ticketId());

        // Симуляция подтверждения резервации во внешнем API
        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/confirmReservation")
                            .queryParam("externalEventID", externalEventID)
                            .build())
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(ReservationResponseDto.class)
                    .block();
        } catch (Exception e) {
            log.warn("External confirmation call failed (expected for simulation): {}", e.getMessage());
        }

        return new ReservationResponseDto(requestDto.ticketId());
    }

}
