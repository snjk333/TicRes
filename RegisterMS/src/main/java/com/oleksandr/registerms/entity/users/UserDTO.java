package com.oleksandr.registerms.entity.users;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private UUID id;

    private String username;

    private String email;

    private Role role;

    private String firstName;

    private String lastName;

    private String phoneNumber;

}
