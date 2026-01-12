package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListRestaurantByCuisineTypeUseCase")
class ListRestaurantsByCuisineTypeUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase;

    @Test
    @DisplayName("Deve retornar página de restaurantes com sucesso")
    void shouldReturnPageOfRestaurantsSuccessfully() {
        // Arrange
        String cuisineType = "Italian";
        Restaurant restaurant = new RestaurantBuilder().withCuisineType(cuisineType).build();

        var currentPage = 0;
        var pageSize = 10;
        PagedQuery<String> query = new PagedQuery<>(cuisineType, currentPage, pageSize);
        Page<Restaurant> expectedPage = new Page<>(currentPage, pageSize, 1, 1, List.of(restaurant));

        given(restaurantGateway.findByCuisineType(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = listRestaurantsByCuisineTypeUseCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.currentPage()).isEqualTo(currentPage);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).isNotNull().hasSize(1);

        then(loggedUserGateway).should(never()).hasRole(any(ForGettingRoleName.class));
        then(restaurantGateway).should().findByCuisineType(query);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver restaurantes")
    void shouldReturnEmptyPageWhenNoRestaurantsFound() {
        // Arrange
        String cuisineType = "NonExistentCuisine";
        var currentPage = 0;
        var pageSize = 10;
        PagedQuery<String> query = new PagedQuery<>(cuisineType, currentPage, pageSize);
        Page<Restaurant> expectedPage = new Page<>(currentPage, pageSize, 0, 1, List.of());

        given(restaurantGateway.findByCuisineType(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = listRestaurantsByCuisineTypeUseCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.currentPage()).isEqualTo(currentPage);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).isNotNull().isEmpty();

        then(loggedUserGateway).should(never()).hasRole(any(ForGettingRoleName.class));
        then(restaurantGateway).should().findByCuisineType(query);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a consulta for nula")
    void shouldThrowExceptionWhenQueryIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> listRestaurantsByCuisineTypeUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Input cannot be null.");

        then(restaurantGateway).should(never()).findByCuisineType(null);
        then(loggedUserGateway).should(never()).hasRole(any());
    }
}
