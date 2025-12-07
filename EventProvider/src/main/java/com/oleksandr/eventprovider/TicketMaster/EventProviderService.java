package com.oleksandr.eventprovider.TicketMaster;

import com.oleksandr.eventprovider.Event.Event;
import com.oleksandr.eventprovider.Event.EventMapper;
import com.oleksandr.eventprovider.TicketMaster.dto.TicketmasterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventProviderService {

    private final TicketmasterClient client;
    private final EventMapper mapper;
    private final String COUNTRY_CODE = "PL";

    @Autowired
    public EventProviderService(TicketmasterClient client, EventMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public List<Event> getRealEvents() {

        TicketmasterResponse response = client.fetchEvents(COUNTRY_CODE).block();
        if (response == null || response.embedded() == null) {
            return Collections.emptyList();
        }

        return response.embedded().events().stream()
                .map(mapper::ticketmasterDtoToEvent)
                .collect(Collectors.toList());
    }
}