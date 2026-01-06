package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.OpeningHoursBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteRestaurantUseCase")
class DeleteRestaurantUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @Test
    @DisplayName("Deve deletar Restaurant com sucesso")
    void shouldDeleteRestaurantSuccessfully() {
        Long id = 1L;
        Address address = new Address("Street", "123", "City", "State", "12345678", "Complement");
        UserType userType = new UserType(1L, "Owner", Set.of(new Role(1L, "RESTAURANT_OWNER")));
        User owner = new User(UUID.randomUUID(), "Owner Name", "owner@email.com", address, userType);
        var tuesday = new OpeningHoursBuilder().withDayOfDay(DayOfWeek.TUESDAY).build();
        var friday = new OpeningHoursBuilder().build();
        var menuItem = new MenuItemBuilder().build();

        Restaurant restaurant = new Restaurant(id, "Restaurant Name", address, "Cuisine",
                Set.of(tuesday, friday), Set.of(menuItem), owner);

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(id)).willReturn(Optional.of(restaurant));

        deleteRestaurantUseCase.execute(id);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(id);
        then(restaurantGateway).should().delete(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(id))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to delete restaurants.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should(never()).findById(any());
        then(restaurantGateway).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando Restaurant não é encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurant not found.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(id);
        then(restaurantGateway).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(restaurantGateway).should(never()).findById(any());
        then(restaurantGateway).should(never()).delete(any());
    }
}
