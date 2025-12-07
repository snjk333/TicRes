package com.oleksandr.monolith.integration.wrapper;

import com.oleksandr.common.dto.EventDTO;
import com.oleksandr.common.dto.TicketDTO;

import java.util.List;
import java.util.UUID;

public interface WrapperService {

    List<EventDTO> fetchExternalEvents();
    EventDTO fetchEventById(UUID eventId);
    List<TicketDTO> fetchTicketsByEvent(UUID eventId);
}
