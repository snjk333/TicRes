package com.oleksandr.monolith.booking.input.dto;

import lombok.*;

import java.util.UUID;

@Builder
public record BookingCreateRequestDTO(
         UUID ticketId
) { }
