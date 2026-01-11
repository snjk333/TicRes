package com.oleksandr.monolith.user.output.dto;

import lombok.Builder;

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