package com.oleksandr.registerms.dto.LoginRegister;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    String username;

    @NotBlank
    @Size(min = 6, max = 25)
    String password;

    @NotBlank
    private String email;


    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Size(min = 9, max = 15)
    private String phoneNumber;

}

