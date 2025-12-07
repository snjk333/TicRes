package com.oleksandr.monolith.User.DTO;

import lombok.*;

@Builder
public record UserUpdateRequestDTO(
        String username,
        String email
) { }
