package com.oleksandr.monolith.event.mapper;

import com.oleksandr.monolith.event.output.dto.EventDetailsDTO;
import com.oleksandr.monolith.event.output.dto.EventSummaryDTO;
import com.oleksandr.monolith.event.model.Event;
import com.oleksandr.common.dto.EventDTO;
import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.monolith.ticket.model.Ticket;
import com.oleksandr.monolith.ticket.mapper.TicketMapper;
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
        event.setId(dto.id());
        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setLocation(dto.location());
        event.setImageURL(dto.imageURL());
        event.setEventDate(dto.eventDate());

        List<Ticket> tickets = new ArrayList<>();
        if (dto.tickets() != null) {
            for (TicketDTO tDto : dto.tickets()) {
                if (tDto == null) continue;
                Ticket t = ticketMapper.mapToEntity(tDto);
                if (tDto.id() != null) t.setId(tDto.id());
                t.setEvent(event);
                tickets.add(t);
            }
        }
        event.setTickets(tickets);
        return event;
    }

    public Event updateEventInformation(Event eventToChange, EventDTO dto) {
        if (dto == null) return eventToChange;

        if (dto.name() != null) eventToChange.setName(dto.name());
        if (dto.description() != null) eventToChange.setDescription(dto.description());
        if (dto.location() != null) eventToChange.setLocation(dto.location());
        if (dto.imageURL() != null) eventToChange.setImageURL(dto.imageURL());
        if (dto.eventDate() != null) eventToChange.setEventDate(dto.eventDate());
        if (dto.tickets() != null) {
            if (eventToChange.getTickets() == null) {
                eventToChange.setTickets(new ArrayList<>());
            }
            List<Ticket> existing = eventToChange.getTickets();

            for (var tDto : dto.tickets()) {
                if (tDto == null) continue;
                if (tDto.id() != null) {
                    Optional<Ticket> opt = existing.stream()
                            .filter(x -> x.getId() != null && x.getId().equals(tDto.id()))
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
        event.setId(dto.id());
        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setLocation(dto.location());
        event.setImageURL(dto.imageURL());
        event.setEventDate(dto.eventDate());

        List<Ticket> tickets = new ArrayList<>();
        if (dto.tickets() != null) {
            for (var tDto : dto.tickets()) {
                if (tDto == null) continue;
                Ticket t = ticketMapper.mapToEntity(tDto);
                if (tDto.id() != null) t.setId(tDto.id());
                t.setEvent(event);
                tickets.add(t);
            }
        }
        event.setTickets(tickets);
        return event;
    }

    public void updateEntityFromDto(Event entity, EventDTO dto) {
        if (entity == null || dto == null) return;

        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setLocation(dto.location());
        entity.setImageURL(dto.imageURL());
        entity.setEventDate(dto.eventDate());

        if (dto.tickets() != null) {
            if (entity.getTickets() == null) entity.setTickets(new ArrayList<>());

            Map<UUID, Ticket> existingTicketsMap = entity.getTickets().stream()
                    .filter(t -> t.getId() != null)
                    .collect(Collectors.toMap(Ticket::getId, t -> t));

            Set<UUID> dtoTicketIds = new HashSet<>();

            for (var ticketDto : dto.tickets()) {
                if (ticketDto == null) continue;

                UUID tid = ticketDto.id();
                if (tid != null) {
                    dtoTicketIds.add(tid);
                    Ticket existingTicket = existingTicketsMap.get(tid);
                    if (existingTicket != null) {
                        ticketMapper.updateEntityFromDto(existingTicket, ticketDto);
                        continue;
                    } else {
                        Ticket newTicket = ticketMapper.mapToEntity(ticketDto);
                        if (ticketDto.id() != null) newTicket.setId(ticketDto.id());
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
