package br.com.techchallenge.restaurant_cleanarch.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para BusinessException")
class BusinessExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com a mensagem fornecida")
    void shouldCreateExceptionWithProvidedMessage() {
        String expectedMessage = "Erro de negócio genérico";
        BusinessException exception = new BusinessException(expectedMessage);

        assertThat(exception.getMessage())
                .isNotNull()
                .isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Deve ser uma instância de RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        BusinessException exception = new BusinessException("Erro");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
