package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteRestaurantUseCase")
class DeleteRestaurantUseCaseTest {

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @Test
    @DisplayName("Deve excluir restaurante com sucesso quando usuário logado é o DONO e tem role")
    void shouldDeleteRestaurantSuccessfullyWhenCurrentUserIsOwner() {
        Long restaurantId = 1L;

        User owner = new UserBuilder()
                .withDefaults()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        Restaurant restaurant = new RestaurantBuilder()
                .withDefaults()
                .withId(restaurantId)
                .withOwner(owner)
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);

        deleteRestaurantUseCase.execute(restaurantId);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should().delete(restaurantId);
    }

    @Test
    @DisplayName("Deve excluir restaurante com sucesso quando usuário logado é FUNCIONÁRIO e tem role")
    void shouldDeleteRestaurantSuccessfullyWhenCurrentUserIsEmployee() {
        Long restaurantId = 1L;

        User owner = new UserBuilder()
                .withDefaults()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        User employee = new UserBuilder()
                .withDefaults()
                .build();

        Restaurant restaurant = new RestaurantBuilder()
                .withDefaults()
                .withId(restaurantId)
                .withOwner(owner)
                .withEmployee(employee)
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(employee);

        deleteRestaurantUseCase.execute(restaurantId);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should().delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando tem role mas não é dono nem funcionário")
    void shouldThrowWhenCurrentUserIsNotOwnerNorEmployee() {
        Long restaurantId = 1L;

        User owner = new UserBuilder()
                .withDefaults()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        User employee = new UserBuilder().withDefaults().build();
        User stranger = new UserBuilder().withDefaults().build();

        Restaurant restaurant = new RestaurantBuilder()
                .withDefaults()
                .withId(restaurantId)
                .withOwner(owner)
                .withEmployee(employee)
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(stranger);

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should(never()).delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (não consulta findById)")
    void shouldThrowWhenUserHasNoRole() {
        Long restaurantId = 1L;

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existe (não chama requireCurrentUser)")
    void shouldThrowWhenRestaurantNotFound() {
        Long restaurantId = 1L;

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurant not found");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(restaurantGateway).should(never()).delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowWhenInputIsNull() {
        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
