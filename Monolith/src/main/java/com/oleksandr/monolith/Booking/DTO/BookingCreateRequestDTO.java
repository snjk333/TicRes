package com.oleksandr.monolith.Booking.DTO;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingCreateRequestDTO {
    private UUID ticketId;
}
