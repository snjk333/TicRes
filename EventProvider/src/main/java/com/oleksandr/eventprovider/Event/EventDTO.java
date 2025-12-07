package com.oleksandr.eventprovider.Event;

import com.oleksandr.eventprovider.Ticket.TicketDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record EventDTO(

        UUID id,

        @NotBlank(message = "Event name cannot be blank")
        String name,

        @NotBlank(message = "Event description cannot be blank")
        String description,

        @NotBlank(message = "Event location cannot be blank")
        String location,

        String imageURL,

        @NotNull(message = "Event date cannot be null")
        LocalDateTime eventDate,

        @Valid
        List<TicketDTO> tickets
){}


