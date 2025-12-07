package com.oleksandr.registerms.util.uservalidation;

import com.oleksandr.registerms.exception.*;
import com.oleksandr.registerms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    Mono<Void> validateUsernameForRegister(String username) {
        Mono<Void> lengthCheck = validateUsernameLength(username);
        Mono<Void> existenceCheck = userRepository.findByUsername(username)
                .flatMap(user -> Mono.<Void>error(new UserAlreadyExistsException("Username already exists")))
                .switchIfEmpty(Mono.empty());
        return lengthCheck.then(existenceCheck);
    }

    Mono<Void> validateUsernameForLogin(String username) {
        return validateUsernameLength(username);
    }

    private Mono<Void> validateUsernameLength(String username) {
        if (username == null || username.length() < 3 || username.length() > 16) {
            return Mono.error(new InvalidUsernameFormatException("Username must be between 3 and 16 characters"));
        }
        return Mono.empty();
    }

    Mono<Void> validatePassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 25) {
            return Mono.error(new InvalidPasswordFormatException("Password must be between 6 and 25 characters"));
        }
        return Mono.empty();
    }

    public Mono<Void> validateEmailFormat(@NotBlank String email) {
        boolean isValid = EmailValidator.getInstance().isValid(email);
        if(!isValid){
            return Mono.error(new InvalidEmailFormatException("Invalid email"));
        }
        return Mono.empty();
    }

    public Mono<Void> validateEmailExist(@NotBlank String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> Mono.<Void>error(new EmailAlreadyExistsException("Email already exists")))
                .switchIfEmpty(Mono.empty());
    }
}
