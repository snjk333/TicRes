package com.oleksandr.registerms.util.uservalidation;

import com.oleksandr.registerms.exception.InvalidEmailFormatException;
import com.oleksandr.registerms.exception.InvalidPasswordFormatException;
import com.oleksandr.registerms.exception.InvalidUsernameFormatException;
import com.oleksandr.registerms.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    @Mock
    UserRepository repository;

    UserValidator userValidator = new UserValidator(repository);

    @Test
    void validateUsernameSuccessful(){
        String username =  "username";

        assertDoesNotThrow(() ->
                userValidator.validateUsernameForLogin(username)
                        .block()
        );
    }
    @Test
    void validateUsernameWithErrorByLengthLessThan3(){
        String username =  "12";

        assertThrows(InvalidUsernameFormatException.class, () -> userValidator.validateUsernameForLogin(username).block());
    }
    @Test
    void validateUsernameWithErrorByLengthMoreThan16(){
        String username =  "usernameusernameusername";

        assertThrows(InvalidUsernameFormatException.class, () -> userValidator.validateUsernameForLogin(username).block());
    }
    @Test
    void validateUsernameWithErrorByUsernameIsNull(){
        String username =  null;

        assertThrows(InvalidUsernameFormatException.class, () -> userValidator.validateUsernameForLogin(username).block());
    }

@Test
void validatePasswordSuccessful(){
    String username =  "passworddd";

    assertDoesNotThrow(() ->
            userValidator.validatePassword(username)
                    .block()
    );
}
    @Test
    void validatePasswordWithErrorByLengthLessThan3(){
        String password =  "12345";

        assertThrows(InvalidPasswordFormatException.class, () -> userValidator.validatePassword(password).block());
    }
    @Test
    void validatePasswordWithErrorByLengthMoreThan25(){
        String password =  "passspassspassspassspasssspassspasspaasss";

        assertThrows(InvalidPasswordFormatException.class, () -> userValidator.validatePassword(password).block());
    }
    @Test
    void validatePasswordWithErrorByUsernameIsNull(){
        String password =  null;

        assertThrows(InvalidPasswordFormatException.class, () -> userValidator.validatePassword(password).block());
    }


    @Test
    void validateEmailFormatSuccessful() {
        String email = "kulbitTest@gmail.com";
        assertDoesNotThrow(() ->
                userValidator.validateEmailFormat(email)
                        .block()
        );
    }

    @Test
    void validateEmailFormatWithError() {
        String email = "kulbitTestgmail.com@";
        assertThrows(InvalidEmailFormatException.class, () -> userValidator.validateEmailFormat(email).block());
    }



    @Test
    void validateUsernameForRegister() {
    }



}