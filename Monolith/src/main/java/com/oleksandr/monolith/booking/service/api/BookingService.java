package com.oleksandr.monolith.booking.service.api;

import com.oleksandr.monolith.booking.model.Booking;
import com.oleksandr.monolith.ticket.model.Ticket;
import com.oleksandr.monolith.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface BookingService {


    @Transactional
    Booking createBooking(User user, Ticket ticket);

    @Transactional
    Booking cancelBooking(Booking booking);

    @Transactional
    Booking completeBooking(Booking booking);

    @Transactional(readOnly = true)
    Booking findById(UUID bookingId);

    @Transactional(readOnly = true)
    List<Booking> getBookingsByUser(UUID userId);
}
