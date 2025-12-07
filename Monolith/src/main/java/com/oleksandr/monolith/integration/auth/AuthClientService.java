package com.oleksandr.monolith.integration.auth;

import com.oleksandr.monolith.User.DTO.AuthUserDTO;
import com.oleksandr.monolith.User.DTO.UserDTO;

import java.util.UUID;

public interface AuthClientService {
    AuthUserDTO getUserById(UUID userId);

    AuthUserDTO updateUser(AuthUserDTO userDto);
}
