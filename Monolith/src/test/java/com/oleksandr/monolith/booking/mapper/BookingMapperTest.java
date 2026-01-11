package com.oleksandr.monolith.booking.mapper;

import com.oleksandr.monolith.booking.input.dto.BookingDTO;
import com.oleksandr.monolith.booking.input.dto.BookingSummaryDTO;
import com.oleksandr.monolith.booking.model.Booking;
import com.oleksandr.monolith.ticket.model.Ticket;
import com.oleksandr.monolith.user.model.User;
import com.oleksandr.common.enums.BOOKING_STATUS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
    }

    @Test
    void mapToDto_shouldMapBookingToDTO() {
        // Given
        UUID bookingId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTicket(ticket);
        booking.setUser(user);
        booking.setStatus(BOOKING_STATUS.PAID);

        // When
        BookingDTO result = bookingMapper.mapToDto(booking);

        // Then
        assertNotNull(result);
        assertEquals(bookingId, result.id());
        assertEquals(ticketId, result.ticketId());
        assertEquals(userId, result.userId());
        assertEquals(BOOKING_STATUS.PAID, result.status());
    }

    @Test
    void mapToDto_shouldHandleNullTicket() {
        // Given
        UUID bookingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTicket(null);
        booking.setUser(user);
        booking.setStatus(BOOKING_STATUS.CREATED);

        // When
        BookingDTO result = bookingMapper.mapToDto(booking);

        // Then
        assertNotNull(result);
        assertEquals(bookingId, result.id());
        assertNull(result.ticketId());
        assertEquals(userId, result.userId());
        assertEquals(BOOKING_STATUS.CREATED, result.status());
    }

    @Test
    void mapToDto_shouldHandleNullUser() {
        // Given
        UUID bookingId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTicket(ticket);
        booking.setUser(null);
        booking.setStatus(BOOKING_STATUS.CANCELLED);

        // When
        BookingDTO result = bookingMapper.mapToDto(booking);

        // Then
        assertNotNull(result);
        assertEquals(bookingId, result.id());
        assertEquals(ticketId, result.ticketId());
        assertNull(result.userId());
        assertEquals(BOOKING_STATUS.CANCELLED, result.status());
    }

    @Test
    void mapToDto_shouldThrowExceptionWhenBookingIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingMapper.mapToDto(null));
    }

    @Test
    void mapToEntity_shouldMapDTOToBooking() {
        // Given
        UUID bookingId = UUID.randomUUID();
        BookingDTO dto = BookingDTO.builder()
                .id(bookingId)
                .ticketId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status(BOOKING_STATUS.PAID)
                .build();

        // When
        Booking result = bookingMapper.mapToEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(BOOKING_STATUS.PAID, result.getStatus());
    }

    @Test
    void mapToEntity_shouldThrowExceptionWhenDTOIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingMapper.mapToEntity(null));
    }

    @Test
    void mapEntityListToDtoList_shouldMapListOfBookings() {
        // Given
        UUID bookingId1 = UUID.randomUUID();
        UUID bookingId2 = UUID.randomUUID();

        Booking booking1 = new Booking();
        booking1.setId(bookingId1);
        booking1.setStatus(BOOKING_STATUS.PAID);

        Booking booking2 = new Booking();
        booking2.setId(bookingId2);
        booking2.setStatus(BOOKING_STATUS.CREATED);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        // When
        List<BookingDTO> result = bookingMapper.mapEntityListToDtoList(bookings);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingId1, result.get(0).id());
        assertEquals(bookingId2, result.get(1).id());
    }

    @Test
    void mapEntityListToDtoList_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<BookingDTO> result = bookingMapper.mapEntityListToDtoList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapDtoListToEntityList_shouldMapListOfDTOs() {
        // Given
        UUID bookingId1 = UUID.randomUUID();
        UUID bookingId2 = UUID.randomUUID();

        BookingDTO dto1 = BookingDTO.builder()
                .id(bookingId1)
                .status(BOOKING_STATUS.PAID)
                .build();

        BookingDTO dto2 = BookingDTO.builder()
                .id(bookingId2)
                .status(BOOKING_STATUS.CREATED)
                .build();

        List<BookingDTO> dtos = Arrays.asList(dto1, dto2);

        // When
        List<Booking> result = bookingMapper.mapDtoListToEntityList(dtos);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingId1, result.get(0).getId());
        assertEquals(bookingId2, result.get(1).getId());
    }

    @Test
    void mapDtoListToEntityList_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<Booking> result = bookingMapper.mapDtoListToEntityList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapToSummaryDto_shouldMapBookingToSummaryDTO() {
        // Given
        UUID bookingId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTicket(ticket);
        booking.setUser(user);
        booking.setStatus(BOOKING_STATUS.PAID);
        booking.setCreatedAt(createdAt);

        // When
        BookingSummaryDTO result = bookingMapper.mapToSummaryDto(booking);

        // Then
        assertNotNull(result);
        assertEquals(bookingId, result.id());
        assertEquals(ticketId, result.ticketId());
        assertEquals(userId, result.userId());
        assertEquals(BOOKING_STATUS.PAID, result.status());
        assertEquals(createdAt, result.createdAt());
    }

    @Test
    void mapToSummaryDto_shouldThrowExceptionWhenBookingIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingMapper.mapToSummaryDto(null));
    }

    @Test
    void mapListToSummaryListDto_shouldMapListOfBookingsToSummaryDTOs() {
        // Given
        UUID bookingId1 = UUID.randomUUID();
        UUID bookingId2 = UUID.randomUUID();
        LocalDateTime createdAt1 = LocalDateTime.now();
        LocalDateTime createdAt2 = LocalDateTime.now().minusDays(1);

        Booking booking1 = new Booking();
        booking1.setId(bookingId1);
        booking1.setStatus(BOOKING_STATUS.PAID);
        booking1.setCreatedAt(createdAt1);

        Booking booking2 = new Booking();
        booking2.setId(bookingId2);
        booking2.setStatus(BOOKING_STATUS.CREATED);
        booking2.setCreatedAt(createdAt2);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        // When
        List<BookingSummaryDTO> result = bookingMapper.mapListToSummaryListDto(bookings);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookingId1, result.get(0).id());
        assertEquals(createdAt1, result.get(0).createdAt());
        assertEquals(bookingId2, result.get(1).id());
        assertEquals(createdAt2, result.get(1).createdAt());
    }

    @Test
    void mapListToSummaryListDto_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<BookingSummaryDTO> result = bookingMapper.mapListToSummaryListDto(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
