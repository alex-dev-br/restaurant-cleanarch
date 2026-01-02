package br.com.techchallenge.restaurant_cleanarch.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserTypeInUseException")
class UserTypeInUseExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem padrão correta")
    void shouldCreateExceptionWithCorrectMessage() {
        UserTypeInUseException exception = new UserTypeInUseException();

        assertThat(exception.getMessage())
                .isNotNull()
                .contains("The user type is in use and cannot be deleted.");
    }
}
