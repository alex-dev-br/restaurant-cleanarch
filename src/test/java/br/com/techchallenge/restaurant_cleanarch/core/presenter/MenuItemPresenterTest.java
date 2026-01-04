package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        // Act
        MenuItemOutput output = MenuItemPresenter.toOutput(menuItem);

        // Assert
        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(1L);
        assertThat(output.name()).isEqualTo("Pizza Margherita");
        assertThat(output.description()).isEqualTo("Clássica italiana");
        assertThat(output.price()).isEqualTo(new BigDecimal("45.90"));
        assertThat(output.restaurantOnly()).isFalse();
        assertThat(output.photoPath()).isEqualTo("/photos/pizza.jpg");
        assertThat(output.restaurantId()).isEqualTo(menuItem.getRestaurant().getId());  // ← Valida o novo campo
    }
}