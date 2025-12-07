package com.oleksandr.monolith.integration.auth;

import com.oleksandr.common.dto.AuthUserDTO;

import java.util.UUID;

public interface AuthClientService {
    AuthUserDTO getUserById(UUID userId);

    AuthUserDTO updateUser(AuthUserDTO userDto);
}
