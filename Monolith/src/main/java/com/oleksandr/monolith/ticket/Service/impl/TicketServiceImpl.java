package com.oleksandr.monolith.ticket.Service.impl;

import com.oleksandr.common.enums.TICKET_STATUS;
import com.oleksandr.monolith.ticket.Service.api.TicketService;
import com.oleksandr.monolith.ticket.model.Ticket;
import com.oleksandr.monolith.ticket.repository.TicketRepository;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.common.exceptions.TicketNotAvailableException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Value("${ticket.service.retry-count:3}")
    private int maxAttempts;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    @Override
    public Ticket reserveTicket(UUID ticketId) {
        int attempt = 0;

        while (true) {
            try {
                return reserveOnce(ticketId);
            } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
                attempt++;

                if (attempt >= maxAttempts) {
                    log.error(
                            "Failed to reserve ticket after {} attempts, ticketId={}",
                            attempt, ticketId
                    );
                    throw ex;
                }

                log.warn(
                        "Optimistic lock while reserving ticket, retry attempt={}, ticketId={}",
                        attempt, ticketId
                );

                delay(attempt);
            }
        }
    }

    private void delay(int attempt) {
        try {
            Thread.sleep(50L * attempt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", e);
        }
    }

    private Ticket reserveOnce(UUID ticketId) {
        log.info("Attempting to reserve ticketId={}", ticketId);

        Ticket ticket = findById(ticketId);

        if (ticket.getStatus() != TICKET_STATUS.AVAILABLE) {
            log.warn(
                    "Ticket {} is not available. Current status: {}",
                    ticketId, ticket.getStatus()
            );
            throw new TicketNotAvailableException("Ticket not available: " + ticketId);
        }

        ticket.setStatus(TICKET_STATUS.RESERVED);
        Ticket savedTicket = ticketRepository.saveAndFlush(ticket);

        log.info("Ticket {} successfully reserved", ticketId);
        return savedTicket;
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