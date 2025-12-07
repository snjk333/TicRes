package com.oleksandr.monolith.integration.wrapper;

import java.util.UUID;

public record ReservationRequestDto(UUID ticketId, UUID reservationId) { }
