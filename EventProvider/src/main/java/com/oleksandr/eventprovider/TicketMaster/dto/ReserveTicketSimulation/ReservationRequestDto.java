package com.oleksandr.eventprovider.TicketMaster.dto.ReserveTicketSimulation;

import java.util.UUID;

public record ReservationRequestDto(UUID ticketId, UUID reservationId) {
}
