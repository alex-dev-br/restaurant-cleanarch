package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para MenuItemPresenter")
class MenuItemPresenterTest {

    @Test
    @DisplayName("Deve converter MenuItem para MenuItemOutput corretamente")
    void shouldConvertMenuItemToMenuItemOutput() {
        Long id = 1L;
        String name = "Pizza Margherita";
        String description = "Molho de tomate, mussarela e manjericão";
        BigDecimal price = new BigDecimal("45.00");
        Boolean restaurantOnly = false;
        String photoPath = "/images/pizza.jpg";

        MenuItem menuItem = new MenuItem(id, name, description, price, restaurantOnly, photoPath);

        MenuItemOutput output = MenuItemPresenter.toOutput(menuItem);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(name);
        assertThat(output.description()).isEqualTo(description);
        assertThat(output.price()).isEqualByComparingTo("45");
        assertThat(output.restaurantOnly()).isEqualTo(restaurantOnly);
        assertThat(output.photoPath()).isEqualTo(photoPath);
    }
}
