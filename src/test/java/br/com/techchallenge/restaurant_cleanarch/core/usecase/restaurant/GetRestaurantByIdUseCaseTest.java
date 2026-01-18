package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetRestaurantByIdUseCase")
class GetRestaurantByIdUseCaseTest {

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

    @Test
    @DisplayName("Deve retornar restaurante quando existir e usuário tiver permissão")
    void shouldReturnRestaurantWhenExistsAndUserHasPermission() {
        Long restaurantId = 1L;

        var owner = new UserBuilder()
                .withDefaults()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        Restaurant restaurant = new RestaurantBuilder()
                .withDefaults()
                .withId(restaurantId)
                .withOwner(owner)
                .build();

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));

        var result = getRestaurantByIdUseCase.execute(restaurantId);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(restaurantId);

        then(loggedUserGateway).should(never()).hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        Long restaurantId = 1L;

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        Optional<Restaurant> result = getRestaurantByIdUseCase.execute(restaurantId);

        assertThat(result).isEmpty();

        then(loggedUserGateway).should(never()).hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> getRestaurantByIdUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
