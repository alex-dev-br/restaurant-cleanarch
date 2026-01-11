package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetAllRestaurantUseCase")
class ListRestaurantsUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListRestaurantsUseCase listRestaurantsUseCase;

    @Test
    @DisplayName("Deve retornar lista de Restaurant com sucesso")
    void shouldReturnRestaurantListSuccessfully() {
        // Arrange
        Long id = 1L;
        Address address = new Address("Street", "123", "City", "State", "12345678", "Complement");
        UserType userType = new UserType(1L, "Owner", Set.of(new Role(1L, "RESTAURANT_OWNER")));
        User owner = new UserBuilder()
                .withId(UUID.randomUUID())
                .withName("Owner Name")
                .withEmail("owner@email.com")
                .withAddress(address)
                .withUserType(userType)
                .withPasswordHash("HASHED_DEFAULT") // opcional (builder já tem default)
                .build();
        var tuesday = new OpeningHoursBuilder().withDayOfDay(DayOfWeek.TUESDAY).build();
        var friday = new OpeningHoursBuilder().build();
        var menuItem = new MenuItemBuilder().build();

        Restaurant restaurant = new Restaurant(id, "Restaurant Name", address, "Cuisine",
                Set.of(tuesday, friday), Set.of(menuItem), owner);

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findAll()).willReturn(List.of(restaurant));

        // Act
        List<Restaurant> result = listRestaurantsUseCase.execute();

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        Restaurant output = result.getFirst();
        assertThat(output.getId()).isEqualTo(restaurant.getId());
        assertThat(output.getName()).isEqualTo(restaurant.getName());
        assertThat(output.getCuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(output.getOpeningHours()).hasSize(2);
        assertThat(output.getMenu()).hasSize(1);
        assertThat(output.getOwner().getId()).isEqualTo(owner.getId());

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver restaurantes")
    void shouldReturnEmptyListWhenNoRestaurants() {
        // Arrange
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findAll()).willReturn(Collections.emptyList());

        // Act
        List<Restaurant> result = listRestaurantsUseCase.execute();

        // Assert
        assertThat(result).isNotNull().isEmpty();

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        // Arrange
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> listRestaurantsUseCase.execute())
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to view restaurants.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should(never()).findAll();
    }
}
