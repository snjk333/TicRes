package com.oleksandr.monolith.booking.input.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BookingCreateRequestDTO(
         UUID ticketId
) { }
