package com.oleksandr.monolith.Event.Service;

import com.oleksandr.monolith.Event.DTO.Response.EventDetailsDTO;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import com.oleksandr.monolith.Event.EntityRepo.Event;
import com.oleksandr.monolith.Event.DTO.EventDTO;
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
