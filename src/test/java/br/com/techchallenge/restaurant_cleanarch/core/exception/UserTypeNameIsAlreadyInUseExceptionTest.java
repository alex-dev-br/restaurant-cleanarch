package br.com.techchallenge.restaurant_cleanarch.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserTypeNameIsAlreadyInUseException")
class UserTypeNameIsAlreadyInUseExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem padrão correta")
    void shouldCreateExceptionWithCorrectMessage() {
        UserTypeNameIsAlreadyInUseException exception = new UserTypeNameIsAlreadyInUseException();

        assertThat(exception.getMessage())
                .isNotNull()
                .isEqualTo("User type name is already in use.");
    }
}
