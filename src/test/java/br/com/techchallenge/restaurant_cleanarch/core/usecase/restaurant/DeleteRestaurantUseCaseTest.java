package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
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
    @DisplayName("Deve excluir restaurante com sucesso quando usuário tem role e restaurante existe")
    void shouldDeleteRestaurantSuccessfully() {
        // Arrange
        Long restaurantId = 1L;

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .build();

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));

        // Act
        deleteRestaurantUseCase.execute(restaurantId);

        // Assert
        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(restaurantGateway).should().delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        // Arrange
        Long restaurantId = 1L;
        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existe e não deletar")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 1L;

        given(loggedUserGateway.hasRole(RestaurantRoles.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurant not found.");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(restaurantGateway).should(never()).delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (validação do UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
