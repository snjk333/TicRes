package com.oleksandr.monolith.event.service.impl;

import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.event.mapper.EventMapper;
import com.oleksandr.monolith.event.model.Event;
import com.oleksandr.monolith.event.output.dto.EventDetailsDTO;
import com.oleksandr.monolith.event.output.dto.EventSummaryDTO;
import com.oleksandr.monolith.event.repository.EventRepository;
import com.oleksandr.monolith.event.service.api.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Event findById(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventSummaryDTO> getAllEventsSummary() {
        return eventMapper.mapListToSummaryList(this.getAllEvents());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EventSummaryDTO> getAllEventsSummaryPaginated(int page, int size) {
        log.debug("Getting paginated events: page={}, size={}", page, size);
        
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        
        Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Event> eventPage = eventRepository.findAll(pageable);
        
        log.info("Retrieved {} events (page {}/{}) sorted by eventDate", 
            eventPage.getNumberOfElements(), 
            eventPage.getNumber(), 
            eventPage.getTotalPages());
        
        return eventPage.map(eventMapper::mapToSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetailsDTO getEventDetails(UUID id) {
        Event event = findById(id);
        return eventMapper.mapEventToDetailsDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        List<Event> events = eventRepository.findByEventDateAfter(LocalDateTime.now());
        return events;
    }

    @Transactional
    @Override
    public Event saveEventEntity(Event event) {
        return eventRepository.saveAndFlush(event);
    }


}
