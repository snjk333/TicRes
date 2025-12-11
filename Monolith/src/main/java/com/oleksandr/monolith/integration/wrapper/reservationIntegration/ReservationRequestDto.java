package com.oleksandr.monolith.integration.wrapper.reservationIntegration;

import java.util.UUID;

public record ReservationRequestDto(UUID ticketId, UUID reservationId) { }
