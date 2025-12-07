package com.oleksandr.eventprovider.Ticket;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TicketMapper {

    // DTO → Entity
    public Ticket mapToEntity(TicketDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TicketDTO cannot be null");
        Ticket ticket = new Ticket();
        ticket.setId(dto.id());
        ticket.setType(dto.type());
        ticket.setPrice(dto.price());
        ticket.setPlace(dto.place());
        ticket.setStatus(dto.status() != null ? dto.status() : TICKET_STATUS.AVAILABLE);

        return ticket;
    }

    public List<Ticket> mapTicketsListFromDto(List<TicketDTO> dtos) {
        return dtos == null ? List.of() :
                dtos.stream()
                        .map(this::mapToEntity)
                        .filter(Objects::nonNull)
                        .toList();
    }

    // Entity → DTO
    public TicketDTO mapToDto(Ticket ticket) {
        if (ticket == null) throw new IllegalArgumentException("Ticket entity cannot be null");

        return TicketDTO.builder()
                .id(ticket.getId())
                .type(ticket.getType())
                .price(ticket.getPrice())
                .place(ticket.getPlace())
                .status(ticket.getStatus())
                .eventId(ticket.getEvent() != null ? ticket.getEvent().getId() : null)
                .build();
    }

    public List<TicketDTO> mapEntityListToDtoList(List<Ticket> tickets) {
        return tickets == null ? List.of() :
                tickets.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }
}
