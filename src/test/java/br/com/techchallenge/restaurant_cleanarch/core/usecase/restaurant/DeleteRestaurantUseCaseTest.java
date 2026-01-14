package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.BeforeEach;
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

    private Long restaurantId;
    private Address address;
    private User owner;
    private OpeningHours tuesday;
    private OpeningHours friday;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;
        address = new Address("Street", "123", "City", "State", "12345678", "Complement");
        var userType = new UserType(1L, "Owner", Set.of(new Role(1L, UserRoles.RESTAURANT_OWNER.getRoleName())));
        tuesday = new OpeningHoursBuilder().withDayOfWeek(DayOfWeek.TUESDAY).build();
        friday = new OpeningHoursBuilder().build();
        menuItem = new MenuItemBuilder().build();
        UUID uuid = UUID.randomUUID();
        owner = new UserBuilder()
                .withId(uuid)
                .withName("Owner Name")
                .withEmail("owner@email.com")
                .withAddress(address)
                .withUserType(userType)
                .withPasswordHash("HASHED_DEFAULT") // opcional (builder já tem default)
                .build();
    }

    @Test
    @DisplayName("Deve deletar Restaurant com sucesso")
    void shouldDeleteRestaurantSuccessfully() {
        Restaurant restaurant = new Restaurant(restaurantId, "Restaurant Name", address, "Cuisine", owner);
        restaurant.addOpeningHours(tuesday);
        restaurant.addOpeningHours(friday);
        restaurant.addMenuItem(menuItem);

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);

        deleteRestaurantUseCase.execute(restaurantId);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should().findById(restaurantId);
        then(restaurantGateway).should().delete(restaurantId);
    }

    @Test
    @DisplayName("Deve deletar Restaurant com sucesso se o usuário logado for funcionário")
    void shouldDeleteRestaurantSuccessfullyIfLoggedUserIsEmployee() {
        UserBuilder userBuilder = new UserBuilder();
        User loggedUser = userBuilder.withId(UUID.randomUUID()).build();

        Restaurant restaurant = new Restaurant(restaurantId, "Restaurant Name", address, "Cuisine", owner);
        restaurant.addOpeningHours(tuesday);
        restaurant.addOpeningHours(friday);
        restaurant.addMenuItem(menuItem);
        restaurant.addEmployee(loggedUser);

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(loggedUser);

        deleteRestaurantUseCase.execute(restaurantId);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should().findById(restaurantId);
        then(restaurantGateway).should().delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        Long id = 1L;

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(id))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should(never()).findById(any());
        then(restaurantGateway).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserNoIsOwnerOrEmployee() {
        UserBuilder userBuilder = new UserBuilder();
        User loggedUser = userBuilder.withId(UUID.randomUUID()).build();

        Restaurant restaurant = new Restaurant(restaurantId, "Restaurant Name", address, "Cuisine", owner);
        restaurant.addOpeningHours(tuesday);
        restaurant.addOpeningHours(friday);
        restaurant.addMenuItem(menuItem);

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(loggedUser);

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
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
                .hasMessageContaining("Restaurant not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(id);
        then(restaurantGateway).should(never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(restaurantGateway).should(never()).findById(any());
        then(restaurantGateway).should(never()).delete(any());
    }
}
