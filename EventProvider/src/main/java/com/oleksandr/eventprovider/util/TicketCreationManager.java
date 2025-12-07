package com.oleksandr.eventprovider.util;

import com.oleksandr.eventprovider.Event.Event;
import com.oleksandr.eventprovider.Ticket.Ticket;
import com.oleksandr.eventprovider.Ticket.TICKET_STATUS;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TicketCreationManager {

    public void fillTicketsForAllEvents(List<Event> clearEvents) {
        for (Event event : clearEvents) {
            fillTickets(event);
        }
    }

    public void fillTickets(Event event) {

        if (event.getTickets() == null || event.getTickets().isEmpty()) {

            List<Ticket> tickets = new ArrayList<>();

            //20
            for (int i = 1; i <= 20; i++) {
                String type;
                double price;

                if (i <= 2) { // 2 VIP
                    type = "VIP";
                    price = 200.0;
                } else if (i <= 14) { // 12 Standard
                    type = "STANDARD";
                    price = 100.0;
                } else { // 6 Cheap
                    type = "CHEAP";
                    price = 50.0;
                }
                String place = String.valueOf(i);
                
                Ticket ticket = new Ticket();
                ticket.setEvent(event);
                ticket.setType(type);
                ticket.setPrice(price);
                ticket.setPlace(place);
                ticket.setStatus(TICKET_STATUS.AVAILABLE);

                tickets.add(ticket);
            }

            event.setTickets(tickets);
        }
    }
}
