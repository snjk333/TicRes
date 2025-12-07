package com.oleksandr.monolith.Booking.Service;

import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import com.oleksandr.monolith.Ticket.EntityRepo.Ticket;
import com.oleksandr.monolith.User.EntityRepo.User;
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
