package com.oleksandr.eventprovider.rest;

import com.oleksandr.eventprovider.Event.EventDTO;
import com.oleksandr.eventprovider.Event.EventService;
import com.oleksandr.eventprovider.Ticket.TicketDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/external")
@Validated
public class MainController {

    private final EventService eventService;

    public MainController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public Page<EventDTO> getAllEvents(
            @RequestParam(value = "includeTickets", defaultValue = "false") boolean includeTickets,

            @RequestParam(value = "page", defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100")
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        return eventService.getAllEventsPaginated(includeTickets, pageable);
    }

    @PostMapping("/events/refresh")
    public ResponseEntity<Map<String, String>> refreshEventsFromApi() {
        eventService.refreshEventsFromApi();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Events successfully refreshed from Ticketmaster API");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{id}")
    public EventDTO getEventByUUID(
            @PathVariable("id") UUID id,
            @RequestParam(value = "includeTickets", defaultValue = "false") boolean includeTickets
    ) {
        return eventService.getEvent(id, includeTickets);
    }

    @GetMapping("/events/{id}/tickets")
    public List<TicketDTO> getTicketsByEvent(
            @PathVariable("id") UUID id
    ) {
        return eventService.getTicketsByEvent(id);
    }
}