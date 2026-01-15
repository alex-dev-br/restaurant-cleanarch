package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
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
@DisplayName("Testes para ListRestaurantsByCuisineTypeUseCase")
class ListRestaurantsByCuisineTypeUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListRestaurantsByCuisineTypeUseCase useCase;

    @Test
    @DisplayName("Deve retornar página de restaurantes com sucesso")
    void shouldReturnPageOfRestaurantsSuccessfully() {
        // Arrange
        String cuisineType = "Italian";
        Restaurant restaurant = new RestaurantBuilder()
                .withCuisineType(cuisineType)
                .build();

        int pageNumber = 0;
        int pageSize = 10;
        long totalElements = 1L;

        PagedQuery<String> query = new PagedQuery<>(cuisineType, pageNumber, pageSize);

        Page<Restaurant> expectedPage = new Page<>(
                pageNumber,
                pageSize,
                totalElements,
                List.of(restaurant)
        );

        given(restaurantGateway.findByCuisineType(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.content()).containsExactly(restaurant);

        // public access: não deve consultar role
        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should().findByCuisineType(query);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver restaurantes")
    void shouldReturnEmptyPageWhenNoRestaurantsFound() {
        // Arrange
        String cuisineType = "NonExistentCuisine";

        int pageNumber = 0;
        int pageSize = 10;
        long totalElements = 0L;

        PagedQuery<String> query = new PagedQuery<>(cuisineType, pageNumber, pageSize);

        Page<Restaurant> expectedPage = new Page<>(
                pageNumber,
                pageSize,
                totalElements,
                List.of()
        );

        given(restaurantGateway.findByCuisineType(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero(); // totalElements=0 => totalPages=0 (derivado)
        assertThat(result.content()).isEmpty();

        // public access: não deve consultar role
        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should().findByCuisineType(query);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando a consulta for nula (validação do UseCaseBase)")
    void shouldThrowExceptionWhenQueryIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Input cannot be null.");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).shouldHaveNoInteractions();
    }
}
