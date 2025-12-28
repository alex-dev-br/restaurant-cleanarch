package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

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
        MenuItem item = MenuItem.builder()
                .name("Pizza Margherita")
                .description("Pizza clássica")
                .price(new BigDecimal("30"))
                .restaurantOnly(false)
                .photoPath("/photos/pizza.jpg")
                .build();

        // Act & Assert
        assertDoesNotThrow(item::validate);
    }

    @Test
    @DisplayName("Deve lançar BusinessException sem nome do item")
    void deveLancarExcecaoSemNome() {
        // Arrange
        MenuItem invalid = MenuItem.builder().price(new BigDecimal("10")).build();

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do item do menu é obrigatório.");
    }

    @Test
    @DisplayName("Deve lançar BusinessException com preço inválido")
    void deveLancarExcecaoPrecoInvalido() {
        // Arrange
        MenuItem invalid = MenuItem.builder().name("Item").price(BigDecimal.ZERO).build();

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Preço do item do menu deve ser maior que zero.");
    }

}
