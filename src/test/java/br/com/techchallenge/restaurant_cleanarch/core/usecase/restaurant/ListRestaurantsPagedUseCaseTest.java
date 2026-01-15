package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
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
@DisplayName("Testes para ListRestaurantsPagedUseCase")
class ListRestaurantsPagedUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListRestaurantsPagedUseCase useCase;

    @Test
    @DisplayName("Deve retornar página de restaurantes quando usuário tiver permissão")
    void shouldReturnPageOfRestaurantsWhenUserHasPermission() {
        // Arrange
        int currentPage = 0;
        int pageSize = 10;

        PagedQuery<Void> query = new PagedQuery<>(null, currentPage, pageSize);

        Restaurant restaurant = new RestaurantBuilder()
                .withName("R1")
                .build();

        Page<Restaurant> expectedPage = new Page<>(
                currentPage,
                pageSize,
                1L,
                List.of(restaurant)
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findAll(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(currentPage);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).containsExactly(restaurant);

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll(query);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver restaurantes e usuário tiver permissão")
    void shouldReturnEmptyPageWhenNoRestaurantsAndUserHasPermission() {
        // Arrange
        int currentPage = 0;
        int pageSize = 10;

        PagedQuery<Void> query = new PagedQuery<>(null, currentPage, pageSize);

        Page<Restaurant> expectedPage = new Page<>(
                currentPage,
                pageSize,
                0L,
                List.of()
        );

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findAll(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero(); // com totalElements=0, tende a ser 0
        assertThat(result.content()).isEmpty();

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll(query);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tiver permissão")
    void shouldThrowOperationNotAllowedWhenUserHasNoPermission() {
        // Arrange
        PagedQuery<Void> query = new PagedQuery<>(null, 0, 10);

        given(loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT)).willReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(query))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(RestaurantRoles.VIEW_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando query for nula (validação do UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenQueryIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
