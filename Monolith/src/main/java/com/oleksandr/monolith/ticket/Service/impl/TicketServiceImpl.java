package com.oleksandr.monolith.ticket.Service.impl;

import com.oleksandr.common.enums.TICKET_STATUS;
import com.oleksandr.monolith.ticket.Service.api.TicketService;
import com.oleksandr.monolith.ticket.model.Ticket;
import com.oleksandr.monolith.ticket.repository.TicketRepository;
import com.oleksandr.monolith.common.exceptions.ConcurrentUpdateException;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.common.exceptions.TicketNotAvailableException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    @Override
    public Ticket reserveTicket(UUID ticketId) {
        log.info("Attempting to reserve ticketId={}", ticketId);
        Ticket ticket = findById(ticketId);

        if (ticket.getStatus() != TICKET_STATUS.AVAILABLE) {
            log.warn("Ticket {} is not available. Current status: {}", ticketId, ticket.getStatus());
            throw new TicketNotAvailableException("Ticket not available: " + ticketId);
        }

        try {
            ticket.setStatus(TICKET_STATUS.RESERVED);
            Ticket savedTicket = ticketRepository.saveAndFlush(ticket);
            log.info("Ticket {} successfully reserved", ticketId);
            return savedTicket;
        } catch (OptimisticLockingFailureException | OptimisticLockException ole) {
            log.warn("Optimistic locking conflict while reserving ticket: ticketId={}, message={}",
                    ticketId, ole.getMessage());
            throw new ConcurrentUpdateException("Ticket was reserved by another user", ole);
        }
    }

    @Transactional
    @Override
    public void markAvailable(Ticket ticket) {
        log.info("Marking ticket {} as AVAILABLE", ticket.getId());
        ticket.setStatus(TICKET_STATUS.AVAILABLE);
        ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    @Override
    public void markSold(Ticket ticket) {
        log.info("Marking ticket {} as SOLD", ticket.getId());
        if (ticket.getStatus() == TICKET_STATUS.SOLD) {
            log.warn("Ticket {} is already SOLD", ticket.getId());
            throw new TicketNotAvailableException("Ticket already sold: " + ticket.getId());
        }
        ticket.setStatus(TICKET_STATUS.SOLD);
        ticketRepository.saveAndFlush(ticket);
    }

    @Transactional(readOnly = true)
    @Override
    public Ticket findById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found with ID: {}", ticketId);
                    return new ResourceNotFoundException("Ticket not found: " + ticketId);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isTicketAvailable(UUID ticketId) {
        Ticket ticket = findById(ticketId);
        boolean available = ticket.getStatus() == TICKET_STATUS.AVAILABLE;
        log.debug("Ticket {} availability: {}", ticketId, available);
        return available;
    }
}