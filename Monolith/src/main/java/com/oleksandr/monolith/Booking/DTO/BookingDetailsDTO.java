package com.oleksandr.monolith.Booking.DTO;

import com.oleksandr.common.enums.BOOKING_STATUS;
import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BookingDetailsDTO (
        UUID id,
        UserSummaryDTO user,
        TicketDTO ticket,
        BOOKING_STATUS status,
        LocalDateTime createdAt,
        Long version
) { }
