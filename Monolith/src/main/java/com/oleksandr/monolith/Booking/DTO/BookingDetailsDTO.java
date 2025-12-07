package com.oleksandr.monolith.Booking.DTO;

import com.oleksandr.monolith.Booking.EntityRepo.BOOKING_STATUS;
import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetailsDTO {
    private UUID id;
    private UserSummaryDTO user;
    private TicketDTO ticket;
    private BOOKING_STATUS status;
    private LocalDateTime createdAt;
    private Long version;
}
