package com.oleksandr.monolith.booking.input.dto;

import com.oleksandr.common.enums.BOOKING_STATUS;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BookingDTO(

        UUID id,

        @NotNull
        UUID ticketId,

        @NotNull
        UUID userId,

        @NotNull
        BOOKING_STATUS status

) { }
