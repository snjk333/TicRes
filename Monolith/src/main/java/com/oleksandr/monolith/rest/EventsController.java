package com.oleksandr.monolith.rest;

import com.oleksandr.monolith.Coordinator.EventTicketCoordinator;
import com.oleksandr.monolith.Event.DTO.Response.EventDetailsDTO;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import com.oleksandr.monolith.Event.Service.EventService;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class EventsController {

    private final EventService eventService;
    private final EventTicketCoordinator eventTicketCoordinator;

    public EventsController(EventService eventService, EventTicketCoordinator eventTicketCoordinator) {
        this.eventService = eventService;
        this.eventTicketCoordinator = eventTicketCoordinator;
    }


    @GetMapping
    public Page<EventSummaryDTO> getEventsPaginated(
        @RequestParam(defaultValue = "0") 
        @Min(value = 0, message = "Page number cannot be negative") 
        int page,
        
        @RequestParam(defaultValue = "10") 
        @Min(value = 1, message = "Page size must be at least 1")
        @Max(value = 100, message = "Page size cannot exceed 100") 
        int size
    ) {
        log.info("GET /events?page={}&size={}", page, size);
        return eventService.getAllEventsSummaryPaginated(page, size);
    }


    @GetMapping("/{id}")
    public EventDetailsDTO getEventDetails(@PathVariable("id") UUID id) {
        log.info("GET /events/{}", id);
        return eventService.getEventDetails(id);
    }


    @Deprecated
    @GetMapping("/all")
    public List<EventSummaryDTO> getAllEventsLegacy() {
        log.warn("GET /events/all - DEPRECATED endpoint called! Use /events?page=0&size=1000 instead");
        return eventService.getAllEventsSummary();
    }


    @GetMapping("/{id}/tickets")
    public List<TicketDTO> getTicketsByEvent( @PathVariable("id") UUID id ) {
        return eventTicketCoordinator.getTicketsByEventId(id);
    }

}
