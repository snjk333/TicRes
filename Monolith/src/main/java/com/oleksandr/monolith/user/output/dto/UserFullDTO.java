package com.oleksandr.monolith.user.output.dto;

import com.oleksandr.common.enums.USER_ROLE;
import com.oleksandr.monolith.booking.input.dto.BookingDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserFullDTO(

        UUID id,

        @NotBlank
        String username,

        @Email
        @NotBlank
        String email,

        String firstName,
        String lastName,
        String phoneNumber,

        @NotNull
        USER_ROLE role,

        List<BookingDTO> bookings

) {}



