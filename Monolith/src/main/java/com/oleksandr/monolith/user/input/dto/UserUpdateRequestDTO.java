package com.oleksandr.monolith.user.input.dto;

import lombok.*;

@Builder
public record UserUpdateRequestDTO(
        String username,
        String email
) { }
