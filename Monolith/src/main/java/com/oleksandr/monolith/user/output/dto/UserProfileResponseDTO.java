package com.oleksandr.monolith.user.output.dto;

import com.oleksandr.common.enums.USER_ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfileResponseDTO(

        UUID id,

        @NotBlank
        String username,

        String firstName,

        String lastName,

        String phoneNumber,

        @Email
        @NotBlank
        String email,

        @NotNull
        USER_ROLE role

) { }



