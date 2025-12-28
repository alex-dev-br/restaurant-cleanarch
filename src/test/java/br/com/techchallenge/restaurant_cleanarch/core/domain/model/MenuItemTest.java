package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MenuItemTest {

    @Test
    @DisplayName("Deve criar MenuItem válido sem lançar exceção")
    void deveCriarMenuItemValido() {
        // Arrange
        UserType ownerType = UserType.builder().name("Dono de Restaurante").build();
        User owner = User.builder().userType(ownerType).build();
        Restaurant restaurant = Restaurant.builder().owner(owner).build();
        MenuItem item = MenuItem.builder()
                .name("Pizza Margherita")
                .description("Pizza clássica")
                .price(new BigDecimal("30"))
                .restaurantOnly(false)
                .photoPath("/photos/pizza.jpg")
                .restaurant(restaurant)
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

    @Test
    @DisplayName("Deve lançar BusinessException sem restaurante associado")
    void deveLancarExcecaoSemRestaurante() {
        // Arrange
        MenuItem invalid = MenuItem.builder().name("Item").price(new BigDecimal("10")).build();

        // Act & Assert
        assertThatThrownBy(invalid::validate)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Item deve estar associado a um restaurante.");
    }
}
