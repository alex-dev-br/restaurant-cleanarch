package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.*;
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
@DisplayName("Testes para GetRestaurantByIdUseCase")
class GetRestaurantByIdUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

    @Test
    @DisplayName("Deve retornar Restaurant com todos os dados quando encontrado e autorizado")
    void shouldReturnRestaurantWithAllFieldsSuccessfully() {
        // Arrange
        Long id = 1L;

        Address address = new Address("Street", "123", "City", "State", "12345678", "Complement");

        Role role = new Role(1L, "RESTAURANT_OWNER");
        UserType userType = new UserType(1L, "Owner", Set.of(role));

        User owner = new UserBuilder()
                .withId(UUID.randomUUID())
                .withName("Owner Name")
                .withEmail("owner@email.com")
                .withAddress(address)
                .withUserType(userType)
                .withPasswordHash("HASHED_DEFAULT")
                .build();

        OpeningHours tuesday = new OpeningHoursBuilder()
                .withDayOfDay(DayOfWeek.TUESDAY)
                .build();

        OpeningHours friday = new OpeningHoursBuilder()
                .withDayOfDay(DayOfWeek.FRIDAY)
                .build();

        MenuItem menuItem = new MenuItemBuilder()
                .withId(10L)
                .withName("Pizza")
                .build();

        User employee = new UserBuilder()
                .withId(UUID.randomUUID())
                .withName("Employee 1")
                .build();

        Restaurant expectedRestaurant = new Restaurant(id, "Restaurant Name", address, "Cuisine", owner);
        expectedRestaurant.addOpeningHours(tuesday);
        expectedRestaurant.addOpeningHours(friday);
        expectedRestaurant.addMenuItem(menuItem);
        expectedRestaurant.addEmployee(employee);

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(id)).willReturn(Optional.of(expectedRestaurant));

        // Act
        Restaurant result = getRestaurantByIdUseCase.execute(id);

        // Assert — identidade
        assertThat(result).isSameAs(expectedRestaurant);

        // Assert — dados básicos
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Restaurant Name");
        assertThat(result.getCuisineType()).isEqualTo("Cuisine");

        // Assert — address
        assertThat(result.getAddress())
                .extracting(Address::getStreet, Address::getCity, Address::getState)
                .containsExactly("Street", "City", "State");

        // Assert — owner
        assertThat(result.getOwner()).isNotNull();
        assertThat(result.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(result.getOwner().getName()).isEqualTo("Owner Name");

        // Assert — opening hours
        assertThat(result.getOpeningHours())
                .hasSize(2)
                .extracting(OpeningHours::getDayOfWeek)
                .containsExactlyInAnyOrder(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);

        // Assert — menu
        assertThat(result.getMenuItems())
                .hasSize(1)
                .extracting(MenuItem::getId)
                .containsExactly(10L);

        // Assert — employees
        assertThat(result.getEmployees())
                .hasSize(1)
                .extracting(User::getName)
                .containsExactly("Employee 1");

        // Assert — comportamento
        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão")
    void shouldThrowExceptionWhenUserHasNoPermission() {
        // Arrange
        Long id = 1L;
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> getRestaurantByIdUseCase.execute(id))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("The current user does not have permission to view restaurants.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should(never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando Restaurant não é encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Arrange
        Long id = 1L;
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(id)).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> getRestaurantByIdUseCase.execute(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurant not found.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        // Act + Assert
        assertThatThrownBy(() -> getRestaurantByIdUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");

        then(loggedUserGateway).should(never()).hasRole(any());
        then(restaurantGateway).should(never()).findById(any());
    }


}
