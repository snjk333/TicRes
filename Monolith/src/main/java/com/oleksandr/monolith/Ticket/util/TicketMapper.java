package com.oleksandr.monolith.Ticket.util;

import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.common.enums.TICKET_STATUS;
import com.oleksandr.monolith.Ticket.EntityRepo.Ticket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        if (dtos == null) return List.of();
        List<Ticket> out = new ArrayList<>();
        for (var d : dtos) {
            if (d == null) continue;
            out.add(mapToEntity(d));
        }
        return out;
    }

    // Entity → DTO
    public TicketDTO mapToDto(Ticket ticket) {
        if (ticket == null) throw new IllegalArgumentException("Ticket entity cannot be null");

        return TicketDTO.builder()
                .id(ticket.getId())
                .eventId(ticket.getEvent() != null ? ticket.getEvent().getId() : null)
                .type(ticket.getType())
                .price(ticket.getPrice())
                .place(ticket.getPlace())
                .status(ticket.getStatus())
                .build();
    }

    public List<TicketDTO> mapEntityListToDtoList(List<Ticket> tickets) {
        return tickets == null ? List.of() :
                tickets.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public void updateEntityFromDto(Ticket entity, TicketDTO dto) {
        if (entity == null || dto == null) return;
        entity.setPrice(dto.price());
        entity.setPlace(dto.place());
        entity.setType(dto.type());
        entity.setStatus(dto.status() != null ? dto.status() : entity.getStatus());
    }
}
