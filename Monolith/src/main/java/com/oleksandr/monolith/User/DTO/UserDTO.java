package com.oleksandr.monolith.User.DTO;

import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.common.enums.USER_ROLE;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private UUID id;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;

    @NotNull
    private USER_ROLE role;

    private List<BookingDTO> bookings;
}
