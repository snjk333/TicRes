package com.oleksandr.monolith.event.service.api;

import com.oleksandr.monolith.event.model.Event;
import com.oleksandr.monolith.event.output.dto.EventDetailsDTO;
import com.oleksandr.monolith.event.output.dto.EventSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EventService {

    List<Event> getAllEvents();
    List<Event> getUpcomingEvents();
    Event findById(UUID eventID);
    @Transactional
    Event saveEventEntity(Event event);

    List<EventSummaryDTO> getAllEventsSummary();
    
    Page<EventSummaryDTO> getAllEventsSummaryPaginated(int page, int size);

    EventDetailsDTO getEventDetails(UUID id);
}
