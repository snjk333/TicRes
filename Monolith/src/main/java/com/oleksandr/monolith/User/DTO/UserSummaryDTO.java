package com.oleksandr.monolith.User.DTO;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryDTO {
    private UUID id;
    private String username;
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;
}