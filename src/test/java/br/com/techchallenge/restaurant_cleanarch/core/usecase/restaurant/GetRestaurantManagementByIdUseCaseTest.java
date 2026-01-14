package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserNotAuthenticatedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetRestaurantManagementByIdUseCase")
class GetRestaurantManagementByIdUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetRestaurantManagementByIdUseCase useCase;

    @Test
    @DisplayName("Deve permitir quando usuário tem mesmo id do owner (UUID) mesmo sendo outra instância")
    void devePermitir_quandoUsuarioTemMesmoIdDoOwner_mesmoSendoOutraInstancia() {
        // Arrange
        Long restaurantId = 1L;
        UUID ownerId = UUID.randomUUID();

        User ownerFromRestaurant = new UserBuilder()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER) // necessário para o domínio
                .build();

        User currentUser = new UserBuilder()
                .withId(ownerId) // mesma UUID, outra instância
                .build();

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withOwner(ownerFromRestaurant)
                .withEmployee(Set.of())
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        // Act
        Restaurant result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isSameAs(restaurant);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve permitir quando usuário tem mesmo id de employee (UUID) mesmo sendo outra instância")
    void devePermitir_quandoUsuarioTemMesmoIdDeEmployee_mesmoSendoOutraInstancia() {
        // Arrange
        Long restaurantId = 2L;
        UUID employeeId = UUID.randomUUID();

        User ownerFromRestaurant = new UserBuilder()
                .withId(UUID.randomUUID())
                .withRole(UserRoles.RESTAURANT_OWNER) // necessário para o domínio
                .build();

        User employeeFromRestaurant = new UserBuilder()
                .withId(employeeId)
                .build();

        User currentUser = new UserBuilder()
                .withId(employeeId) // mesma UUID, outra instância
                .build();

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withOwner(ownerFromRestaurant)
                .withEmployee(Set.of(employeeFromRestaurant))
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        // Act
        Restaurant result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isSameAs(restaurant);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve negar quando usuário não é owner nem employee (por UUID)")
    void deveNegar_quandoUsuarioNaoEhOwnerNemEmployee_porId() {
        // Arrange
        Long restaurantId = 3L;

        User owner = new UserBuilder()
                .withId(UUID.randomUUID())
                .withRole(UserRoles.RESTAURANT_OWNER) // necessário para o domínio
                .build();

        User employee = new UserBuilder()
                .withId(UUID.randomUUID())
                .build();

        User currentUser = new UserBuilder()
                .withId(UUID.randomUUID()) // diferente dos dois
                .build();

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withOwner(owner)
                .withEmployee(Set.of(employee))
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Access denied");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve negar quando não autenticado (requireCurrentUser lança UserNotAuthenticatedException)")
    void deveNegar_quandoNaoAutenticado() {
        // Arrange
        Long restaurantId = 4L;

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withOwner(new UserBuilder()
                        .withId(UUID.randomUUID())
                        .withRole(UserRoles.RESTAURANT_OWNER) // necessário para o domínio
                        .build())
                .withEmployee(Set.of())
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willThrow(new UserNotAuthenticatedException());

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(UserNotAuthenticatedException.class);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve negar quando não tem role necessária (bloqueia antes de IO)")
    void deveNegar_quandoNaoTemRoleNecessaria() {
        // Arrange
        Long restaurantId = 5L;
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }
}
