package com.oleksandr.monolith.Booking.DTO;


import com.oleksandr.common.enums.BOOKING_STATUS;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
public record BookingSummaryDTO(

        UUID id,
        UUID ticketId,
        UUID userId,
        BOOKING_STATUS status,
        LocalDateTime createdAt

) {}