package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListMenuItemsByRestaurantUseCase")
class ListMenuItemsByRestaurantUseCaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @Captor
    private ArgumentCaptor<PagedQuery<Long>> restaurantIdCaptor;

    @InjectMocks
    private ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase;

    @Test
    @DisplayName("Deve retornar página de itens de menu com sucesso")
    void shouldReturnPageOfMenuItemsSuccessfully() {
        // Arrange
        Long restaurantId = 1L;
        Restaurant restaurant = new RestaurantBuilder().withId(restaurantId).build();
        MenuItem menuItem = new MenuItemBuilder().build();
        int currentPage = 0;
        int pageSize = 1;
        int totalElements = 10;
        int totalPages = 1;
        var pagedQuery = new PagedQuery<>(restaurantId, currentPage, pageSize);
        Page<MenuItem> expectedPage = new Page<>(currentPage, pageSize, totalElements, totalPages, List.of(menuItem));

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurant(pagedQuery)).willReturn(expectedPage);

        // Act
        Page<MenuItem> result = listMenuItemsByRestaurantUseCase.execute(pagedQuery);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.currentPage()).isEqualTo(currentPage);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(totalPages);
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isEqualTo(menuItem);

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurant(restaurantIdCaptor.capture());
        then(loggedUserGateway).should(never()).hasRole(any());

        assertThat(restaurantIdCaptor.getValue().filter()).isNotNull().isEqualTo(pagedQuery.filter());

    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver itens de menu")
    void shouldReturnEmptyPageWhenNoMenuItems() {
        // Arrange
        Long restaurantId = 1L;
        Restaurant restaurant = new RestaurantBuilder().withId(restaurantId).build();
        int currentPage = 0;
        int pageSize = 1;
        int totalElements = 10;
        int totalPages = 1;
        Page<MenuItem> expectedPage = new Page<>(currentPage, pageSize, totalElements, totalPages, List.of());
        var pagedQuery = new PagedQuery<>(restaurantId, currentPage, pageSize);

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurant(pagedQuery)).willReturn(expectedPage);

        // Act
        Page<MenuItem> result = listMenuItemsByRestaurantUseCase.execute(pagedQuery);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.currentPage()).isEqualTo(currentPage);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(totalPages);
        assertThat(result.content()).isEmpty();

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurant(restaurantIdCaptor.capture());
        then(loggedUserGateway).should(never()).hasRole(any());

        assertThat(restaurantIdCaptor.getValue().filter()).isNotNull().isEqualTo(pagedQuery.filter());

    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurante não for encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 1L;
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());
        var pagedQuery = new PagedQuery<>(restaurantId, 0, 10);

        // Act & Assert
        assertThatThrownBy(() -> listMenuItemsByRestaurantUseCase.execute(pagedQuery))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurante not found");

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should(never()).findByRestaurant(pagedQuery);
        then(loggedUserGateway).should(never()).hasRole(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID do restaurante for nulo")
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> listMenuItemsByRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Input cannot be null.");

        then(restaurantGateway).should(never()).findById(null);
        then(menuItemGateway).should(never()).findByRestaurant(null);
        then(loggedUserGateway).should(never()).hasRole(any());
    }
}
