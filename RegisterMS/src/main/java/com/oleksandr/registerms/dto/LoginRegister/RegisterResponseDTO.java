package com.oleksandr.registerms.dto.LoginRegister;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class RegisterResponseDTO {
    String accessToken;
    String refreshToken;
    long expiresIn;

}
