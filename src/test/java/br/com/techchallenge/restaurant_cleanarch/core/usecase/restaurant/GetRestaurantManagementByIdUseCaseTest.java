package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserNotAuthenticatedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes para GetRestaurantManagementByIdUseCase")
class GetRestaurantManagementByIdUseCaseTest {

    private RestaurantGateway restaurantGateway;
    private LoggedUserGateway loggedUserGateway;
    private GetRestaurantManagementByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        restaurantGateway = mock(RestaurantGateway.class);
        loggedUserGateway = mock(LoggedUserGateway.class);
        useCase = new GetRestaurantManagementByIdUseCase(restaurantGateway, loggedUserGateway);
    }

    @Test
    void devePermitir_quandoUsuarioTemMesmoIdDoOwner_mesmoSendoOutraInstancia() {
        // Arrange
        Long restaurantId = 1L;
        UUID ownerId = UUID.randomUUID();

        User ownerFromRestaurant = mock(User.class);
        when(ownerFromRestaurant.getId()).thenReturn(ownerId);

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(ownerId); // mesma UUID, outra instância

        Restaurant restaurant = mock(Restaurant.class);

        when(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).thenReturn(true);
        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.requireCurrentUser()).thenReturn(currentUser);

        when(restaurant.getOwner()).thenReturn(ownerFromRestaurant);
        when(restaurant.getEmployees()).thenReturn(Set.of());

        // Act
        Restaurant result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isSameAs(restaurant);
        verify(restaurantGateway).findByIdWithManagement(restaurantId);
        verify(loggedUserGateway).requireCurrentUser();
        verify(loggedUserGateway).hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
    }

    @Test
    void devePermitir_quandoUsuarioTemMesmoIdDeEmployee_mesmoSendoOutraInstancia() {
        // Arrange
        Long restaurantId = 2L;
        UUID employeeId = UUID.randomUUID();

        User employeeFromRestaurant = mock(User.class);
        when(employeeFromRestaurant.getId()).thenReturn(employeeId);

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(employeeId); // mesma UUID, outra instância

        Restaurant restaurant = mock(Restaurant.class);

        when(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).thenReturn(true);
        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.requireCurrentUser()).thenReturn(currentUser);

        when(restaurant.getOwner()).thenReturn(mock(User.class));
        when(restaurant.getEmployees()).thenReturn(Set.of(employeeFromRestaurant));

        // Act
        Restaurant result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isSameAs(restaurant);
        verify(restaurantGateway).findByIdWithManagement(restaurantId);
        verify(loggedUserGateway).requireCurrentUser();
        verify(loggedUserGateway).hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
    }

    @Test
    void deveNegar_quandoUsuarioNaoEhOwnerNemEmployee_porId() {
        // Arrange
        Long restaurantId = 3L;

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(UUID.randomUUID());

        User employee = mock(User.class);
        when(employee.getId()).thenReturn(UUID.randomUUID());

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(UUID.randomUUID()); // diferente

        Restaurant restaurant = mock(Restaurant.class);

        when(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).thenReturn(true);
        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.requireCurrentUser()).thenReturn(currentUser);

        when(restaurant.getOwner()).thenReturn(owner);
        when(restaurant.getEmployees()).thenReturn(Set.of(employee));

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Access denied");

        verify(restaurantGateway).findByIdWithManagement(restaurantId);
        verify(loggedUserGateway).requireCurrentUser();
        verify(loggedUserGateway).hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
    }

    @Test
    void deveNegar_quandoNaoAutenticado() {
        // Arrange
        Long restaurantId = 4L;

        Restaurant restaurant = mock(Restaurant.class);

        when(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).thenReturn(true);
        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.requireCurrentUser()).thenThrow(new UserNotAuthenticatedException());

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(UserNotAuthenticatedException.class);

        verify(restaurantGateway).findByIdWithManagement(restaurantId);
        verify(loggedUserGateway).requireCurrentUser();
        verify(loggedUserGateway).hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
    }

    @Test
    void deveNegar_quandoNaoTemRoleNecessaria() {
        // Arrange
        Long restaurantId = 5L;

        when(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class);

        // Esperado: bloqueia antes de IO
        verifyNoInteractions(restaurantGateway);
        verify(loggedUserGateway, never()).requireCurrentUser();
        verify(loggedUserGateway).hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
    }
}
