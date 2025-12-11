package com.oleksandr.monolith.booking.input.dto;

import com.oleksandr.common.enums.BOOKING_STATUS;
import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.monolith.user.output.dto.UserSummaryDTO;
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
