package com.oleksandr.monolith.user.input.dto;

import lombok.Builder;

@Builder
public record UserUpdateRequestDTO(
        String username,
        String email
) { }
