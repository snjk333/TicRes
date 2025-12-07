package com.oleksandr.monolith.User.DTO;

import com.oleksandr.monolith.User.EntityRepo.USER_ROLE;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserDTO {

    private UUID id;
    private String username;
    private String email;
    private USER_ROLE role;

    private String firstName;
    private String lastName;
    private String phoneNumber;

}
