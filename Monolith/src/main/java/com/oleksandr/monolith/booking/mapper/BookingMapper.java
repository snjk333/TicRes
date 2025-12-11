package com.oleksandr.monolith.booking.mapper;

import com.oleksandr.monolith.booking.input.dto.BookingDTO;
import com.oleksandr.monolith.booking.input.dto.BookingSummaryDTO;
import com.oleksandr.monolith.booking.model.Booking;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class BookingMapper {

    public BookingDTO mapToDto(Booking booking) {
        if (booking == null) throw new IllegalArgumentException("Booking entity cannot be null");

        return BookingDTO.builder()
                .id(booking.getId())
                .ticketId(booking.getTicket() != null ? booking.getTicket().getId() : null)
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .status(booking.getStatus())
                .build();
    }

    public Booking mapToEntity(BookingDTO dto) {
        if (dto == null) throw new IllegalArgumentException("BookingDTO cannot be null");

        Booking booking = new Booking();
        booking.setId(dto.id());
        booking.setStatus(dto.status());
        return booking;
    }

    public List<BookingDTO> mapEntityListToDtoList(List<Booking> bookings) {
        return bookings == null ? List.of() :
                bookings.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public List<Booking> mapDtoListToEntityList(List<BookingDTO> dtos) {
        return dtos == null ? List.of() :
                dtos.stream()
                        .map(this::mapToEntity)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public BookingSummaryDTO mapToSummaryDto(Booking booking) {
        if (booking == null) throw new IllegalArgumentException("Booking entity cannot be null");

        return BookingSummaryDTO.builder()
                .id(booking.getId())
                .ticketId(booking.getTicket() != null ? booking.getTicket().getId() : null)
                .userId(booking.getUser() != null ? booking.getUser().getId() : null)
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    public List<BookingSummaryDTO> mapListToSummaryListDto(List<Booking> bookingsList) {
        return bookingsList == null ? List.of() :
                bookingsList.stream()
                        .map(this::mapToSummaryDto)
                        .filter(Objects::nonNull)
                        .toList();
    }
}