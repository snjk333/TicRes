package com.oleksandr.eventprovider.Event;

import com.oleksandr.eventprovider.Ticket.Ticket;
import com.oleksandr.eventprovider.Ticket.TicketMapper;
import com.oleksandr.eventprovider.TicketMaster.dto.EventMasterDto;
import com.oleksandr.eventprovider.TicketMaster.dto.ImageDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class EventMapper {

    private final TicketMapper ticketMapper;

    public EventMapper(TicketMapper ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    // DTO → Entity
    public Event mapToEntity(EventDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EventDTO cannot be null");

        Event event = new Event();
        event.setId(dto.id());
        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setLocation(dto.location());
        event.setImageURL(dto.imageURL());
        event.setEventDate(dto.eventDate());

        if (dto.tickets() != null) {
            List<Ticket> tickets = ticketMapper.mapTicketsListFromDto(dto.tickets());
            tickets.forEach(t -> t.setEvent(event));
            event.setTickets(tickets);
        } else {
            event.setTickets(new ArrayList<>());
        }

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
            List<Ticket> updatedTickets = ticketMapper.mapTicketsListFromDto(dto.tickets());
            for (Ticket updated : updatedTickets) {
                if (updated.getId() == null) {
                    updated.setEvent(eventToChange);
                    eventToChange.getTickets().add(updated);
                } else {
                    boolean found = false;
                    for (int i = 0; i < eventToChange.getTickets().size(); i++) {
                        Ticket current = eventToChange.getTickets().get(i);
                        if (current.getId().equals(updated.getId())) {
                            updated.setEvent(eventToChange);
                            eventToChange.getTickets().set(i, updated);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        updated.setEvent(eventToChange);
                        eventToChange.getTickets().add(updated);
                    }
                }
            }
        }

        return eventToChange;
    }

    // Entity → DTO
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

    public EventDTO mapToDtoWithoutTickets(Event event) {
        if (event == null) throw new IllegalArgumentException("Event entity cannot be null");

        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .imageURL(event.getImageURL())
                .eventDate(event.getEventDate())
                .tickets(new ArrayList<>())
                .build();
    }

    public Event ticketmasterDtoToEvent(EventMasterDto ticketmasterDto) {
        if (ticketmasterDto == null) {
            return null;
        }
        LocalDateTime eventDate;
        if (ticketmasterDto.dates() != null &&
                ticketmasterDto.dates().start() != null &&
                ticketmasterDto.dates().start().dateTime() != null) {
            eventDate = ticketmasterDto.dates().start().dateTime();
        } else {
            eventDate = null;
        }
        String location = "TBD";
        String description = ticketmasterDto.name() != null ? ticketmasterDto.name() : "No description available.";
        List<Ticket> tickets = new ArrayList<>();


        return Event.builder()
                .name(ticketmasterDto.name())
                .description(description)
                .eventDate(eventDate)
                .location(location)
                .tickets(tickets)
                .externalId(ticketmasterDto.id())
                .imageURL(extractImageUrl(ticketmasterDto.images()))
                .build();
    }



    private String extractImageUrl(List<ImageDto> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }

        Optional<ImageDto> preferredImage = images.stream()
                .filter(img -> "16_9".equals(img.ratio()) || "3_2".equals(img.ratio()))
                .max(Comparator.comparingInt(ImageDto::width));

        return preferredImage.map(ImageDto::url).orElse(images.get(0).url());
    }

    // TODO: Implement extractLocation when venue DTOs are available
    // Location extraction requires VenueDto and proper EventEmbedded structure
}
