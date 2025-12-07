package com.oleksandr.monolith.Coordinator;

import com.oleksandr.monolith.Event.EntityRepo.Event;
import com.oleksandr.common.dto.EventDTO;
import com.oleksandr.monolith.Event.util.EventMapper;
import com.oleksandr.monolith.Event.Service.EventService;
import com.oleksandr.monolith.Ticket.EntityRepo.Ticket;
import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.monolith.Ticket.util.TicketMapper;
import com.oleksandr.monolith.Ticket.Service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventTicketCoordinator {

    private final EventService eventService;
    private final TicketService ticketService;
    private final EventMapper eventMapper;
    private final TicketMapper ticketMapper;

    public EventTicketCoordinator(EventService eventService, TicketService ticketService,
                                  EventMapper eventMapper, TicketMapper ticketMapper) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.eventMapper = eventMapper;
        this.ticketMapper = ticketMapper;
    }

    @Transactional
    public EventDTO createEventWithTickets(EventDTO eventDto) {
        // Map DTO -> Entity
        Event event = eventMapper.mapToEntity(eventDto);

        if (event.getTickets() != null) {
            event.getTickets().forEach(t -> t.setEvent(event));
        }

        Event savedEvent = eventService.saveEventEntity(event);

        return eventMapper.mapToDto(savedEvent);
    }

    @Transactional
    public EventDTO updateEventWithTickets(UUID eventId, EventDTO dto) {
        Event eventEntity = eventService.findById(eventId);

        eventMapper.updateEventInformation(eventEntity, dto);

        // Reconcile
        if (dto.tickets() != null) {
            Map<UUID, Ticket> existingById = eventEntity.getTickets().stream()
                    .filter(t -> t.getId() != null)
                    .collect(Collectors.toMap(Ticket::getId, t -> t));

            List<Ticket> finalList = new ArrayList<>();
            for (TicketDTO tdto : dto.tickets()) {
                if (tdto.id() == null) {
                    Ticket newT = ticketMapper.mapToEntity(tdto);
                    newT.setEvent(eventEntity);
                    finalList.add(newT);
                } else if (existingById.containsKey(tdto.id())) {
                    Ticket exist = existingById.get(tdto.id());
                    if (tdto.price() != 0) exist.setPrice(tdto.price());
                    if (tdto.type() != null) exist.setType(tdto.type());
                    if (tdto.status() != null) exist.setStatus(tdto.status());
                    finalList.add(exist);
                    existingById.remove(tdto.id());
                } else {
                    Ticket t = ticketMapper.mapToEntity(tdto);
                    t.setEvent(eventEntity);
                    finalList.add(t);
                }
            }

            eventEntity.getTickets().clear();
            eventEntity.getTickets().addAll(finalList);
        }

        Event savedEvent = eventService.saveEventEntity(eventEntity);
        return eventMapper.mapToDto(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByEventId(UUID id) {
        Event event = eventService.findById(id);
        if (event == null) return new ArrayList<>();
        List<Ticket> tickets = event.getTickets();
        return tickets.stream().map(ticketMapper::mapToDto).collect(Collectors.toList());
    }
}
