package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserTypeTest {

    @Test
    @DisplayName("Deve criar UserType válido sem lançar exceção")
    void deveCriarUserTypeValido() {
        // Arrange
        UserType userType = UserType.builder()
                .name("Dono de Restaurante")
                .build();

        // Act & Assert
        assertDoesNotThrow(userType::validate);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando nome do UserType for vazio")
    void deveLancarExcecaoQuandoNomeVazio() {
        // Arrange
        UserType invalid = UserType.builder()
                .name("")
                .build();

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do tipo de usuário é obrigatório.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando nome do UserType for null")
    void deveLancarExcecaoQuandoNomeNull() {
        // Arrange
        UserType invalid = UserType.builder().build();   // name null

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do tipo de usuário é obrigatório.");
    }
}
