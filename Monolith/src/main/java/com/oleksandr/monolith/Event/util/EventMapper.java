package com.oleksandr.monolith.Event.util;

import com.oleksandr.monolith.Event.DTO.Response.EventDetailsDTO;
import com.oleksandr.monolith.Event.DTO.Response.EventSummaryDTO;
import com.oleksandr.monolith.Event.EntityRepo.Event;
import com.oleksandr.monolith.Event.DTO.EventDTO;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import com.oleksandr.monolith.Ticket.EntityRepo.Ticket;
import com.oleksandr.monolith.Ticket.util.TicketMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    private final TicketMapper ticketMapper;

    public EventMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    public Event mapToEntity(EventDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EventDTO cannot be null");

        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setImageURL(dto.getImageURL());
        event.setEventDate(dto.getEventDate());

        List<Ticket> tickets = new ArrayList<>();
        if (dto.getTickets() != null) {
            for (TicketDTO tDto : dto.getTickets()) {
                if (tDto == null) continue;
                Ticket t = ticketMapper.mapToEntity(tDto);
                if (tDto.getId() != null) t.setId(tDto.getId());
                t.setEvent(event);
                tickets.add(t);
            }
        }
        event.setTickets(tickets);
        return event;
    }

    public Event updateEventInformation(Event eventToChange, EventDTO dto) {
        if (dto == null) return eventToChange;

        if (dto.getName() != null) eventToChange.setName(dto.getName());
        if (dto.getDescription() != null) eventToChange.setDescription(dto.getDescription());
        if (dto.getLocation() != null) eventToChange.setLocation(dto.getLocation());
        if (dto.getImageURL() != null) eventToChange.setImageURL(dto.getImageURL());
        if (dto.getEventDate() != null) eventToChange.setEventDate(dto.getEventDate());
        if (dto.getTickets() != null) {
            if (eventToChange.getTickets() == null) {
                eventToChange.setTickets(new ArrayList<>());
            }
            List<Ticket> existing = eventToChange.getTickets();

            for (var tDto : dto.getTickets()) {
                if (tDto == null) continue;
                if (tDto.getId() != null) {
                    Optional<Ticket> opt = existing.stream()
                            .filter(x -> x.getId() != null && x.getId().equals(tDto.getId()))
                            .findFirst();
                    if (opt.isPresent()) {
                        ticketMapper.updateEntityFromDto(opt.get(), tDto);
                        continue;
                    }
                }
                Ticket newTicket = ticketMapper.mapToEntity(tDto);
                newTicket.setEvent(eventToChange);
                existing.add(newTicket);
            }
        }

        return eventToChange;
    }

    // Entity -> DTO
    public EventDTO mapToDto(Event event) {
        if (event == null) throw new IllegalArgumentException("Event entity cannot be null");

        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate())
                .tickets(event.getTickets() != null
                        ? ticketMapper.mapEntityListToDtoList(event.getTickets())
                        : List.of())
                .build();
    }

    public List<EventDTO> mapListToDtoList(List<Event> events) {
        return events == null ? List.of() :
                events.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public List<EventSummaryDTO> mapListToSummaryList(List<Event> allEvents) {
        return allEvents == null ? List.of() :
                allEvents.stream()
                        .map(this::mapToSummaryDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    // Entity -> DTO
    public EventSummaryDTO mapToSummaryDto(Event event) {
        if (event == null) throw new IllegalArgumentException("Event entity cannot be null");

        return EventSummaryDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate())
                .build();
    }

    public EventDetailsDTO mapEventToDetailsDto(Event event) {
        if (event == null) throw new IllegalArgumentException("Event entity cannot be null");

        return EventDetailsDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate())
                .build();
    }

    public Event mapToEntityForInsert(EventDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EventDTO cannot be null");

        Event event = new Event();
        event.setId(dto.getId()); // сохраняем внешний ID события
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setImageURL(dto.getImageURL());
        event.setEventDate(dto.getEventDate());

        List<Ticket> tickets = new ArrayList<>();
        if (dto.getTickets() != null) {
            for (var tDto : dto.getTickets()) {
                if (tDto == null) continue;
                Ticket t = ticketMapper.mapToEntity(tDto);
                // сохраняем внешний ID билета (если есть)
                if (tDto.getId() != null) t.setId(tDto.getId());
                t.setEvent(event);
                tickets.add(t);
            }
        }
        event.setTickets(tickets);
        return event;
    }

    public void updateEntityFromDto(Event entity, EventDTO dto) {
        if (entity == null || dto == null) return;

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setLocation(dto.getLocation());
        entity.setImageURL(dto.getImageURL());
        entity.setEventDate(dto.getEventDate());

        if (dto.getTickets() != null) {
            if (entity.getTickets() == null) entity.setTickets(new ArrayList<>());

            Map<UUID, Ticket> existingTicketsMap = entity.getTickets().stream()
                    .filter(t -> t.getId() != null)
                    .collect(Collectors.toMap(Ticket::getId, t -> t));

            Set<UUID> dtoTicketIds = new HashSet<>();

            for (var ticketDto : dto.getTickets()) {
                if (ticketDto == null) continue;

                UUID tid = ticketDto.getId();
                if (tid != null) {
                    dtoTicketIds.add(tid);
                    Ticket existingTicket = existingTicketsMap.get(tid);
                    if (existingTicket != null) {
                        ticketMapper.updateEntityFromDto(existingTicket, ticketDto);
                        continue;
                    } else {
                        Ticket newTicket = ticketMapper.mapToEntity(ticketDto);
                        if (ticketDto.getId() != null) newTicket.setId(ticketDto.getId());
                        newTicket.setEvent(entity);
                        entity.getTickets().add(newTicket);
                    }
                } else {
                    Ticket newTicket = ticketMapper.mapToEntity(ticketDto);
                    newTicket.setEvent(entity);
                    entity.getTickets().add(newTicket);
                }
            }

            entity.getTickets().removeIf(ticket -> ticket.getId() != null && !dtoTicketIds.contains(ticket.getId()));
        }
    }
}
