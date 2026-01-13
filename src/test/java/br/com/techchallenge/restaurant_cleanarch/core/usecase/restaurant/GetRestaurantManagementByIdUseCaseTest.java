package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserNotAuthenticatedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void devePermitir_quandoUsuarioEhOwner() {
        // Arrange
        Long restaurantId = 1L;

        User owner = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.requireCurrentUser()).thenReturn(owner);

        when(restaurant.getOwner()).thenReturn(owner);
        when(restaurant.getEmployees()).thenReturn(Set.of()); // não precisa ser employee

        // se sua UseCaseBase validar role via hasRole, garanta que passe:
        when(loggedUserGateway.hasRole(any())).thenReturn(true);

        // Act
        Restaurant result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isSameAs(restaurant);
    }

    @Test
    void devePermitir_quandoUsuarioEhEmployee() {
        // Arrange
        Long restaurantId = 2L;

        User employee = mock(User.class);
        User owner = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.requireCurrentUser()).thenReturn(employee);

        when(restaurant.getOwner()).thenReturn(owner);
        when(restaurant.getEmployees()).thenReturn(Set.of(employee));

        when(loggedUserGateway.hasRole(any())).thenReturn(true);

        // Act
        Restaurant result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isSameAs(restaurant);
        verify(restaurantGateway).findByIdWithManagement(restaurantId);
        verify(loggedUserGateway).requireCurrentUser();
    }

    @Test
    void deveNegar_quandoUsuarioNaoEhOwnerNemEmployee() {
        // Arrange
        Long restaurantId = 3L;

        User current = mock(User.class);
        User owner = mock(User.class);
        User employee = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.requireCurrentUser()).thenReturn(current);

        when(restaurant.getOwner()).thenReturn(owner);
        when(restaurant.getEmployees()).thenReturn(Set.of(employee));

        when(loggedUserGateway.hasRole(any())).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Access denied");
        verify(restaurantGateway).findByIdWithManagement(restaurantId);
        verify(loggedUserGateway).requireCurrentUser();
    }

    @Test
    void deveNegar_quandoNaoAutenticado() {
        // Arrange
        Long restaurantId = 4L;

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurantGateway.findByIdWithManagement(restaurantId)).thenReturn(Optional.of(restaurant));
        when(loggedUserGateway.hasRole(any())).thenReturn(true);

        when(loggedUserGateway.requireCurrentUser()).thenThrow(new UserNotAuthenticatedException());

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(UserNotAuthenticatedException.class);
        verify(restaurantGateway).findByIdWithManagement(restaurantId);
        verify(loggedUserGateway).requireCurrentUser();
    }

    @Test
    void deveNegar_quandoNaoTemRoleNecessaria() {
        // Arrange
        Long restaurantId = 5L;

        // Se UseCaseBase valida role e lança antes do gateway:
        when(loggedUserGateway.hasRole(any())).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class);

        // Se a UseCaseBase barra antes, o gateway não deve ser chamado
        verifyNoInteractions(restaurantGateway);
        verify(loggedUserGateway, never()).requireCurrentUser();
    }
}
