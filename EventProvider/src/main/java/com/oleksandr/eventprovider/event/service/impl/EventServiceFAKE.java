package com.oleksandr.eventprovider.event.service.impl;

import com.oleksandr.common.dto.EventDTO;
import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.eventprovider.Ticket.mapper.TicketMapper;
import com.oleksandr.eventprovider.Ticket.util.TicketCreationManager;
import com.oleksandr.eventprovider.event.mapper.EventMapper;
import com.oleksandr.eventprovider.event.model.Event;
import com.oleksandr.eventprovider.event.output.repository.FakeEventRepository;
import com.oleksandr.eventprovider.event.service.api.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventServiceFAKE implements EventService {

    private final FakeEventRepository fakeRepository;
    private final TicketCreationManager ticketCreationManager;
    private final EventMapper eventMapper;
    private final TicketMapper ticketMapper;

    public EventServiceFAKE(
            FakeEventRepository fakeRepository,
            TicketCreationManager ticketCreationManager,
            EventMapper eventMapper,
            TicketMapper ticketMapper
    ) {
        this.fakeRepository = fakeRepository;
        this.ticketCreationManager = ticketCreationManager;
        this.eventMapper = eventMapper;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public EventDTO getEvent(UUID id, boolean includeTickets) {
        Event event = fakeRepository.findById(id);

        if(includeTickets) {
            if (event.getTickets() == null || event.getTickets().isEmpty()) {
                ticketCreationManager.fillTickets(event);
            }
        }
        if(!includeTickets) {
            return eventMapper.mapToDtoWithoutTickets(event);
        }
        return eventMapper.mapToDto(event);
    }

    @Override
    public List<TicketDTO> getTicketsByEvent(UUID id) {
        Event event = fakeRepository.findById(id);

        if (event.getTickets() == null || event.getTickets().isEmpty()) {
            ticketCreationManager.fillTickets(event);
        }

        return ticketMapper.mapEntityListToDtoList(event.getTickets());
    }

    @Override
    public List<EventDTO> getAllEvents(boolean includeTickets) {
        List<Event> clearEvents = fakeRepository.getAllEvents();

        if (includeTickets) {
            ticketCreationManager.fillTicketsForAllEvents(clearEvents);
        }

        return eventMapper.mapListToDtoList(clearEvents);
    }

    @Override
    public Page<EventDTO> getAllEventsPaginated(boolean includeTickets, Pageable pageable) {
        throw new UnsupportedOperationException("Fake class not support this operation.");
    }

    @Override
    public List<EventDTO> fetchAndSaveEventsFromApi() {
        throw new UnsupportedOperationException("Fake class not support this operation.");
    }

    @Override
    public void refreshEventsFromApi() {
        throw new UnsupportedOperationException("Fake class not support this operation.");
    }
}
