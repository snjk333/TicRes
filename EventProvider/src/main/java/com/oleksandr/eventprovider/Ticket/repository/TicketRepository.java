package com.oleksandr.eventprovider.Ticket.repository;

import com.oleksandr.eventprovider.Ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}
