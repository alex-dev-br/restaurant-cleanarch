package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListRestaurantsUseCase")
class ListRestaurantsUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListRestaurantsUseCase useCase;

    @Test
    @DisplayName("Deve retornar lista de restaurantes quando usuário tiver permissão")
    void shouldReturnRestaurantsWhenUserHasPermission() {
        // Arrange
        Restaurant r1 = new RestaurantBuilder().withName("R1").build();
        Restaurant r2 = new RestaurantBuilder().withName("R2").build();

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findAll()).willReturn(List.of(r1, r2));

        // Act
        List<Restaurant> result = useCase.execute();

        // Assert
        assertThat(result).containsExactly(r1, r2);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tiver permissão")
    void shouldThrowOperationNotAllowedWhenUserHasNoPermission() {
        // Arrange
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute())
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
