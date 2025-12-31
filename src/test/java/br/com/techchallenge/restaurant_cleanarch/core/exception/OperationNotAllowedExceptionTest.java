package br.com.techchallenge.restaurant_cleanarch.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para OperationNotAllowedException")
class OperationNotAllowedExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem padrão correta")
    void shouldCreateExceptionWithDefaultMessage() {
        OperationNotAllowedException exception = new OperationNotAllowedException();

        assertThat(exception.getMessage())
                .isNotNull()
                .isEqualTo("The current user does not have permission to perform this operation.");
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem personalizada")
    void shouldCreateExceptionWithCustomMessage() {
        String customMessage = "Custom error message";
        OperationNotAllowedException exception = new OperationNotAllowedException(customMessage);

        assertThat(exception.getMessage())
                .isNotNull()
                .isEqualTo(customMessage);
    }
}
