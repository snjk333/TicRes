package com.oleksandr.registerms.dto.LoginRegister;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    String username;

    @NotBlank
    @Size(min = 6, max = 25)
    String password;
}
