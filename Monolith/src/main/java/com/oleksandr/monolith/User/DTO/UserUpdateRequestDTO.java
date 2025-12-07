package com.oleksandr.monolith.User.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequestDTO {
    private String username;
    private String email;
}
