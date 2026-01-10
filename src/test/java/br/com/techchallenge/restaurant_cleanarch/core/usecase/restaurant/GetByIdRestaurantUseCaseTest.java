package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetByIdRestaurantUseCase")
class GetByIdRestaurantUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetByIdRestaurantUseCase getByIdRestaurantUseCase;

    @Test
    @DisplayName("Deve retornar Restaurant com sucesso quando encontrado")
    void shouldReturnRestaurantSuccessfully() {
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

        Restaurant expectedRestaurant = new Restaurant(id, "Restaurant Name", address, "Cuisine",
                Set.of(tuesday, friday), Set.of(menuItem), owner);

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(id)).willReturn(Optional.of(expectedRestaurant));

        Restaurant result = getByIdRestaurantUseCase.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull().isEqualTo(expectedRestaurant.getId());
        assertThat(result.getName()).isNotNull().isEqualTo(expectedRestaurant.getName());
        assertThat(result.getAddress()).isNotNull().isEqualTo(expectedRestaurant.getAddress());
        assertThat(result.getCuisineType()).isNotNull().isEqualTo(expectedRestaurant.getCuisineType());
        assertThat(result.getOpeningHours()).isNotNull().hasSize(2).isEqualTo(expectedRestaurant.getOpeningHours());
        assertThat(result.getMenu()).isNotNull().hasSize(1).isEqualTo(expectedRestaurant.getMenu());
        assertThat(result.getOwner()).isNotNull().isEqualTo(expectedRestaurant.getOwner());

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> getByIdRestaurantUseCase.execute(id))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to update restaurants.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando Restaurant não é encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        Long id = 1L;
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getByIdRestaurantUseCase.execute(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurant not found.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> getByIdRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(restaurantGateway).should(never()).findById(any());
    }
}
