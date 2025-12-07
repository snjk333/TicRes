package com.oleksandr.monolith.Booking.DTO;


import com.oleksandr.monolith.Booking.EntityRepo.BOOKING_STATUS;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingSummaryDTO {
    private UUID id;
    private UUID ticketId;
    private UUID userId;
    private BOOKING_STATUS status;
    private LocalDateTime createdAt;
}