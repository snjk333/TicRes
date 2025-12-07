package com.oleksandr.registerms.util;

import com.oleksandr.registerms.dto.LoginRegister.RegisterRequestDTO;
import com.oleksandr.registerms.entity.users.Role;
import com.oleksandr.registerms.entity.users.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserMapperTest {
    UserMapper userMapper = new UserMapper();

//    @Test
//    void map(){
//
//        RegisterRequestDTO dto =
//                new RegisterRequestDTO("username", "password", "email");
//
//        User actualUser = userMapper.mapFromRegisterDTO(dto, "password");
//
//        Assertions.assertThat(actualUser).isEqualTo
//                (
//                        new User("username", "password", "email", Role.USER, actualUser.getCreatedAt())
//                );
//    }

}
