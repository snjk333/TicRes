package com.oleksandr.monolith.User.DTO;

import lombok.*;

import java.util.UUID;

@Builder
public record UserSummaryDTO(

        UUID id,
        String username,
        String email,

        String firstName,
        String lastName,
        String phoneNumber
) { }