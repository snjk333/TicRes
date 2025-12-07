package com.oleksandr.monolith.Booking.DTO;

import lombok.*;

import java.util.UUID;

@Builder
public record BookingCreateRequestDTO(
         UUID ticketId
) { }
