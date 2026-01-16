package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
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
        var owner = new UserBuilder()
                .withDefaults()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        Restaurant r1 = new RestaurantBuilder().withDefaults().withName("R1").withOwner(owner).build();
        Restaurant r2 = new RestaurantBuilder().withDefaults().withName("R2").withOwner(owner).build();

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findAll()).willReturn(List.of(r1, r2));

        List<Restaurant> result = useCase.execute();

        assertThat(result).containsExactly(r1, r2);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tiver permissão")
    void shouldThrowOperationNotAllowedWhenUserHasNoPermission() {
        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> useCase.execute())
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
