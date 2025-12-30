package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MenuItemTest {

    @Test
    @DisplayName("Deve criar MenuItem válido sem lançar exceção")
    void deveCriarMenuItemValido() {
        // Arrange
//        MenuItem item = new MenuItemBuilder().build();

        // Act & Assert
        assertDoesNotThrow(() -> new MenuItemBuilder().build());
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem nome do item")
    void deveLancarExcecaoSemNome() {
        // Arrange
//        MenuItem invalid = new MenuItemBuilder().withPrice(new BigDecimal("10")).build();

        // Act & Assert
        assertThatThrownBy(() -> new MenuItemBuilder().withName(null).build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException com preço inválido")
    void deveLancarExcecaoPrecoInvalido() {
        // Arrange
//        MenuItem invalid = new MenuItemBuilder().withPrice(BigDecimal.ZERO).build();

        // Act & Assert
        assertThatThrownBy(() -> new MenuItemBuilder().withPrice(BigDecimal.ZERO).build())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Price must be greater than zero.");
    }

}
