package com.oleksandr.monolith.integration.wrapper;

import com.oleksandr.monolith.Event.DTO.EventDTO;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import com.oleksandr.monolith.integration.dto.PageResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class WrapperServiceImpl implements WrapperService {

    private final WebClient webClient;

    public WrapperServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081/external").build();
    }

    @Override
    public List<EventDTO> fetchExternalEvents() {
        List<EventDTO> allEvents = new ArrayList<>();
        int pageNumber = 0;
        int size = 100; // размер страницы
        PageResponse<EventDTO> pageResponse;

        do {
            final int currentPage = pageNumber;
            pageResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/events")
                            .queryParam("includeTickets", "true")
                            .queryParam("page", currentPage)
                            .queryParam("size", size)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PageResponse<EventDTO>>() {})
                    .block();

            if (pageResponse != null && pageResponse.getContent() != null) {
                allEvents.addAll(pageResponse.getContent());
                pageNumber++;
            }
        } while (pageResponse != null && !pageResponse.isLast());

        return allEvents;
    }

    @Override
    public EventDTO fetchEventById(java.util.UUID eventId) {
        return webClient.get()
                .uri("/events/{id}?includeTickets=true", eventId)
                .retrieve()
                .bodyToMono(EventDTO.class)
                .block();
    }

    @Override
    public List<TicketDTO> fetchTicketsByEvent(java.util.UUID eventId) {
        return webClient.get()
                .uri("/events/{id}/tickets", eventId)
                .retrieve()
                .bodyToFlux(TicketDTO.class)
                .collectList()
                .block();
    }
}
