package com.oleksandr.registerms.util;

import com.oleksandr.registerms.dto.LoginRegister.RegisterRequestDTO;
import com.oleksandr.registerms.entity.users.Role;
import com.oleksandr.registerms.entity.users.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User mapFromRegisterDTO(RegisterRequestDTO dto, String hashedPassword) {
        return new User(
                Role.USER,
                dto.getUsername(),
                hashedPassword,
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhoneNumber(),
                LocalDateTime.now()
                );
    }
}