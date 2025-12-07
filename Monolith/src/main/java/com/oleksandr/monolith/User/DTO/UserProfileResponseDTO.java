package com.oleksandr.monolith.User.DTO;

import com.oleksandr.monolith.User.EntityRepo.USER_ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponseDTO {

    private UUID id;

    @NotBlank
    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private USER_ROLE role;

}



