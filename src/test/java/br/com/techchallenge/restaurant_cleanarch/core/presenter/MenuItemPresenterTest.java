package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para MenuItemPresenter")
class MenuItemPresenterTest {

    @Test
    @DisplayName("Deve converter MenuItem para MenuItemOutput corretamente")
    void shouldConvertMenuItemToOutput() {
        // Arrange - Usa o MenuItemBuilder para criar o MenuItem com associação ao Restaurant
        MenuItem menuItem = new MenuItemBuilder()
                .withId(1L)
                .withName("Pizza Margherita")
                .withDescription("Clássica italiana")
                .withPrice(new BigDecimal("45.90"))
                .withRestaurantOnly(false)
                .withPhotoPath("/photos/pizza.jpg")
                .build();  // O builder fornece um Restaurant padrão

        Long restaurantId = 100L;   // Valor fixo para o teste

        // Act
        MenuItemOutput output = MenuItemPresenter.toOutput(menuItem, restaurantId);

        // Assert
        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(1L);
        assertThat(output.name()).isEqualTo("Pizza Margherita");
        assertThat(output.description()).isEqualTo("Clássica italiana");
        assertThat(output.price()).isEqualTo(new BigDecimal("45.90"));
        assertThat(output.restaurantOnly()).isFalse();
        assertThat(output.photoPath()).isEqualTo("/photos/pizza.jpg");
        assertThat(output.restaurantId()).isEqualTo(100L);  // Valor passado no teste
    }
}