package com.oleksandr.monolith.Booking.EntityRepo;

import com.oleksandr.common.enums.BOOKING_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findAllByUserId(UUID userId);

    Optional<Booking> findByUserIdAndTicketId(UUID id, UUID id1);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.ticket.id = :ticketId AND b.status != 'CANCELLED'")
    Optional<Booking> findActiveBookingByUserIdAndTicketId(@Param("userId") UUID userId, @Param("ticketId") UUID ticketId);

    @Query("SELECT b FROM Booking b WHERE b.ticket.id = :ticketId AND b.status != 'CANCELLED'")
    Optional<Booking> findActiveBookingByTicketId(@Param("ticketId") UUID ticketId);


    List<Booking> findByStatusAndCreatedAtBefore(BOOKING_STATUS status, LocalDateTime dateTime);

    long countByStatus(BOOKING_STATUS status);
}
