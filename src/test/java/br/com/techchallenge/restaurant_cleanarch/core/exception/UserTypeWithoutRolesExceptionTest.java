package br.com.techchallenge.restaurant_cleanarch.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserTypeWithoutRolesException")
class UserTypeWithoutRolesExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem padrão correta")
    void shouldCreateExceptionWithCorrectMessage() {
        UserTypeWithoutRolesException exception = new UserTypeWithoutRolesException();

        assertThat(exception.getMessage())
                .isNotNull()
                .isEqualTo("User type must have at least one role valid.");
    }
}
