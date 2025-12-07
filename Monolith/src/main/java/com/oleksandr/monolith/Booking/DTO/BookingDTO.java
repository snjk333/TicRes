package com.oleksandr.monolith.Booking.DTO;

import com.oleksandr.monolith.Booking.EntityRepo.BOOKING_STATUS;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {

    private UUID id;

    @NotNull
    private UUID ticketId;

    @NotNull
    private UUID userId;

    @NotNull
    private BOOKING_STATUS status;
}
