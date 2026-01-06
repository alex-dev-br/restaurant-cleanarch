package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para RestaurantPresenter")
class RestaurantPresenterTest {

    @Test
    @DisplayName("Deve converter Restaurant para RestaurantOutput corretamente")
    void shouldConvertRestaurantToRestaurantOutput() {
        // Address
        Address address = new Address("Rua A", "10", "Cidade B", "Estado C", "12345-678", "Comp");

        // OpeningHours
        OpeningHours openingHours = new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(22, 0));
        Set<OpeningHours> openingHoursSet = Set.of(openingHours);

        // MenuItem
        MenuItem menuItem = new MenuItemBuilder()
                .withId(1L)
                .withName("Item 1")
                .withDescription("Desc")
                .withPrice(new BigDecimal("20.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/img.jpg")
                .build();

        Set<MenuItem> menu = Set.of(menuItem);

        // Owner (User)
        Role role = new Role(1L, "RESTAURANT_OWNER");
        UserType userType = new UserType(1L, "Dono", Set.of(role));
        User owner = new User(UUID.randomUUID(), "Owner Name", "ownerId@test.com", address, userType);

        // Restaurant
        Long restaurantId = 100L;
        String restaurantName = "Restaurante Teste";
        String cuisineType = "Italiana";
        Restaurant restaurant = new Restaurant(restaurantId, restaurantName, address, cuisineType, openingHoursSet, menu, owner);

        RestaurantOutput output = RestaurantPresenter.toOutput(restaurant);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(restaurantId);
        assertThat(output.name()).isEqualTo(restaurantName);
        assertThat(output.cuisineType()).isEqualTo(cuisineType);
        assertThat(output.ownerId()).isEqualTo(owner.getId());

        // Verifica AddressOutput
        assertThat(output.address()).isNotNull();
        assertThat(output.address().street()).isEqualTo(address.getStreet());

        // Verifica OpeningHoursOutput
        assertThat(output.openingHours()).hasSize(1);
        assertThat(output.openingHours().iterator().next().id()).isEqualTo(openingHours.getId());

        // Verifica MenuItemOutput
        assertThat(output.menuItems()).hasSize(1);
        assertThat(output.menuItems().iterator().next().id()).isEqualTo(menuItem.getId());
    }
}
