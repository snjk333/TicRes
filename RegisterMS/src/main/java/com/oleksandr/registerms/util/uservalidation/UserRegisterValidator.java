package com.oleksandr.registerms.util.uservalidation;

import com.oleksandr.registerms.dto.LoginRegister.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRegisterValidator {

    private final UserValidator userValidator;

    public Mono<Void> validateRequest(RegisterRequestDTO dto) {
        return userValidator.validateUsernameForRegister(dto.getUsername())
                .then(userValidator.validatePassword(dto.getPassword()))
                .then(userValidator.validateEmailFormat(dto.getEmail()))
                .then(userValidator.validateEmailExist(dto.getEmail()));
    }

}
