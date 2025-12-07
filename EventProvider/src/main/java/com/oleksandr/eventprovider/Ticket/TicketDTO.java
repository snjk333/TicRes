package com.oleksandr.eventprovider.Ticket;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Builder
public record TicketDTO(
        UUID id,

        UUID eventId,

        @NotBlank String type,

        @Min(0) double price,

        String place,

        @NotNull TICKET_STATUS status) {}

