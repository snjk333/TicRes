package com.oleksandr.registerms.util.uservalidation;

import com.oleksandr.registerms.dto.LoginRegister.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserLoginValidator {

    private final UserValidator userValidator;

    public Mono<Void> validateRequest(LoginRequestDTO dto) {
        return userValidator.validateUsernameForLogin(dto.getUsername())
                .then(userValidator.validatePassword(dto.getPassword()));
    }

}