package com.oleksandr.eventprovider.Event;

import com.oleksandr.eventprovider.Ticket.TicketDTO;
import com.oleksandr.eventprovider.TicketMaster.EventProviderService;
import com.oleksandr.eventprovider.exception.EventNotFoundException;
import com.oleksandr.eventprovider.exception.TicketmasterApiException;
import com.oleksandr.eventprovider.util.TicketCreationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class EventServiceReal implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceReal.class);

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventProviderService eventProviderService;
    private final TicketCreationManager ticketCreationManager;

    public EventServiceReal(EventRepository eventRepository,
                            EventMapper eventMapper,
                            EventProviderService eventProviderService,
                            TicketCreationManager ticketCreationManager) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventProviderService = eventProviderService;
        this.ticketCreationManager = ticketCreationManager;
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getEvent(UUID id, boolean includeTickets) {
        logger.debug("Fetching event with id: {}, includeTickets: {}", id, includeTickets);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        if (includeTickets) {
            return eventMapper.mapToDto(event);
        } else {
            return eventMapper.mapToDtoWithoutTickets(event);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByEvent(UUID id) {
        logger.debug("Fetching tickets for event with id: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        return eventMapper.mapToDto(event).tickets();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents(boolean includeTickets) {
        logger.debug("Fetching all events from database, includeTickets: {}", includeTickets);

        List<Event> events = eventRepository.findAll();

        if (events.isEmpty()) {
            logger.info("No events found in database, fetching from external API");
            return fetchAndSaveEventsFromApi();
        }

        return eventMapper.mapListToDtoList(events);
    }

    @Transactional
    @Override
    public Page<EventDTO> getAllEventsPaginated(boolean includeTickets, Pageable pageable) {
        logger.debug("Fetching paginated events from database. Page: {}, Size: {}, includeTickets: {}",
                pageable.getPageNumber(), pageable.getPageSize(), includeTickets);

        if (pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (pageable.getPageSize() < 1 || pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        Page<Event> eventPage = eventRepository.findAll(pageable);
        if (eventPage.isEmpty() && pageable.getPageNumber() == 0) {
            logger.info("No events found in database, fetching from external API");
            fetchAndSaveEventsFromApi();
            eventPage = eventRepository.findAll(pageable);

            if (eventPage.isEmpty()) {
                long totalCount = eventRepository.count();
                logger.warn("Events were saved but query returned empty! Total events in DB: {}. Check Pageable sort/filter.", totalCount);
            }
        }
        logger.info("Retrieved {} events (page {}/{})",
                eventPage.getNumberOfElements(),
                eventPage.getNumber(),
                eventPage.getTotalPages());
        if (includeTickets) {
            return eventPage.map(eventMapper::mapToDto);
        } else {
            return eventPage.map(eventMapper::mapToDtoWithoutTickets);
        }
    }

    public List<EventDTO> fetchAndSaveEventsFromApi() {
        logger.info("Fetching events from Ticketmaster API");

        try {
            List<Event> eventsFromApi = eventProviderService.getRealEvents();

            if (eventsFromApi == null || eventsFromApi.isEmpty()) {
                logger.warn("No events received from Ticketmaster API - received null or empty list");
                throw new TicketmasterApiException("Ticketmaster API returned empty response");
            }

            logger.info("Received {} events from API", eventsFromApi.size());

            List<String> externalIds = eventsFromApi.stream()
                    .map(Event::getExternalId)
                    .filter(externalId -> externalId != null && !externalId.isBlank())
                    .toList();

            logger.debug("Checking {} external IDs against database", externalIds.size());

            List<Event> existingEvents = eventRepository.findByExternalIdIn(externalIds);
            List<String> existingExternalIds = existingEvents.stream()
                    .map(Event::getExternalId)
                    .toList();

            List<Event> newEvents = eventsFromApi.stream()
                    .filter(event -> !existingExternalIds.contains(event.getExternalId()))
                    .toList();

            logger.info("Found {} new events out of {} from API (filtered by externalId)", newEvents.size(), eventsFromApi.size());

            if (newEvents.isEmpty()) {
                logger.info("No new events to save, returning existing events");
                return eventMapper.mapListToDtoList(existingEvents);
            }

            ticketCreationManager.fillTicketsForAllEvents(newEvents);

            if (!newEvents.isEmpty()) {
                Event firstEvent = newEvents.get(0);
                logger.debug("Sample event before save: id={}, externalId={}, name={}, eventDate={}, tickets={}",
                        firstEvent.getId(), firstEvent.getExternalId(), firstEvent.getName(),
                        firstEvent.getEventDate(), firstEvent.getTickets() != null ? firstEvent.getTickets().size() : 0);
            }
            List<Event> savedEvents = eventRepository.saveAll(newEvents);
            logger.info("Successfully saved {} new events to database", savedEvents.size());
            eventRepository.flush();
            long countAfterSave = eventRepository.count();
            logger.info("Database count after save and flush: {}", countAfterSave);

            return eventMapper.mapListToDtoList(savedEvents);

        } catch (TicketmasterApiException e) {
            logger.error("Ticketmaster API error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching and saving events from API: {}", e.getMessage(), e);
            throw new TicketmasterApiException("Failed to fetch events from external API", e);
        }
    }

    @Transactional
    @Override
    public void refreshEventsFromApi() {
        logger.info("Starting events refresh from Ticketmaster API");

        try {
            long deletedCount = eventRepository.count();
            eventRepository.deleteAll();
            logger.warn("Deleted {} events from database", deletedCount);

            List<EventDTO> newEvents = fetchAndSaveEventsFromApi();
            logger.info("Successfully refreshed {} events from Ticketmaster API", newEvents.size());

        } catch (Exception e) {
            logger.error("Failed to refresh events from API", e);
            throw new TicketmasterApiException("Failed to refresh events", e);
        }
    }
}