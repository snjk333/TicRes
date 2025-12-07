package com.oleksandr.monolith.Booking.Service;

import com.oleksandr.monolith.Booking.EntityRepo.BOOKING_STATUS;
import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import com.oleksandr.monolith.Booking.EntityRepo.BookingRepository;
import com.oleksandr.monolith.Ticket.EntityRepo.TICKET_STATUS;
import com.oleksandr.monolith.common.exceptions.BookingConflictException;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.User.EntityRepo.User;
import com.oleksandr.monolith.Ticket.EntityRepo.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }


    @Transactional
    @Override
    public Booking createBooking(User user, Ticket ticket) {
        log.info("Creating booking entity for userId={} and ticketId={}", user.getId(), ticket.getId());

        if (ticket.getStatus() != TICKET_STATUS.RESERVED) {
            log.warn("Cannot create booking for ticket with status: ticketId={}, status={}", ticket.getId(), ticket.getStatus());
            throw new BookingConflictException("Ticket must be reserved before creating booking: " + ticket.getId());
        }
        
        bookingRepository.findActiveBookingByTicketId(ticket.getId())
                .ifPresent(b -> {
                    log.warn("Active booking conflict detected for ticket: ticketId={}, existingBookingId={}, existingUserId={}, status={}", 
                            ticket.getId(), b.getId(), b.getUser().getId(), b.getStatus());
                    throw new BookingConflictException("Ticket is already booked by another user: " + ticket.getId());
                });

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTicket(ticket);
        booking.setStatus(BOOKING_STATUS.CREATED);
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepository.saveAndFlush(booking);
    }

    @Transactional
    @Override
    public Booking cancelBooking(Booking booking) {
        log.info("Cancelling booking entity with id={}", booking.getId());
        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            log.info("Booking {} is already cancelled", booking.getId());
            return booking;
        }
        booking.setStatus(BOOKING_STATUS.CANCELLED);
        return bookingRepository.saveAndFlush(booking);
    }

    @Transactional
    @Override
    public Booking completeBooking(Booking booking) {
        log.info("Completing booking entity with id={}", booking.getId());
        if (booking.getStatus() == BOOKING_STATUS.PAID) {
            log.info("Booking {} already marked as PAID", booking.getId());
            return booking;
        }
        if (booking.getStatus() == BOOKING_STATUS.CANCELLED) {
            log.warn("Attempted to complete a cancelled booking: bookingId={}", booking.getId());
            throw new IllegalStateException("Cannot complete a cancelled booking: " + booking.getId());
        }
        booking.setStatus(BOOKING_STATUS.PAID);
        return bookingRepository.saveAndFlush(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Booking findById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Booking> getBookingsByUser(UUID userId) {
        return bookingRepository.findAllByUserId(userId);
    }
}