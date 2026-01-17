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

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;

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
    private ArgumentCaptor<PagedQuery<Long>> pagedQueryCaptor;

    @InjectMocks
    private ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase;

    @Test
    @DisplayName("Deve retornar página de itens de menu com sucesso")
    void shouldReturnPageOfMenuItemsSuccessfully() {
        // Arrange
        Long restaurantId = 1L;
        Restaurant restaurant = new RestaurantBuilder().withId(restaurantId).build();

        int pageNumber = 0;
        int pageSize = 1;
        long totalElements = 10L;

        MenuItem menuItem = new MenuItemBuilder().build();
        var query = new PagedQuery<>(restaurantId, pageNumber, pageSize);

        Page<MenuItem> expectedPage = new Page<>(pageNumber, pageSize, totalElements, List.of(menuItem));

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurant(query)).willReturn(expectedPage);

        // Act
        Page<MenuItem> result = listMenuItemsByRestaurantUseCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(10); // ceil(10/1) = 10 (derivado)
        assertThat(result.content()).containsExactly(menuItem);

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurant(pagedQueryCaptor.capture());
        then(loggedUserGateway).should(never()).hasRole(any());

        assertThat(pagedQueryCaptor.getValue()).isEqualTo(query);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver itens de menu")
    void shouldReturnEmptyPageWhenNoMenuItems() {
        // Arrange
        Long restaurantId = 1L;
        Restaurant restaurant = new RestaurantBuilder().withId(restaurantId).build();

        int pageNumber = 0;
        int pageSize = 1;
        long totalElements = 0L;

        var query = new PagedQuery<>(restaurantId, pageNumber, pageSize);
        Page<MenuItem> expectedPage = new Page<>(pageNumber, pageSize, totalElements, List.of());

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurant(query)).willReturn(expectedPage);

        // Act
        Page<MenuItem> result = listMenuItemsByRestaurantUseCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero(); // totalElements=0 => 0 (derivado)
        assertThat(result.content()).isEmpty();

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurant(pagedQueryCaptor.capture());
        then(loggedUserGateway).should(never()).hasRole(any());

        assertThat(pagedQueryCaptor.getValue()).isEqualTo(query);
    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurante não for encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 1L;
        var query = new PagedQuery<>(restaurantId, 0, 10);

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> listMenuItemsByRestaurantUseCase.execute(query))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurant not found");

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should(never()).findByRestaurant(any());
        then(loggedUserGateway).should(never()).hasRole(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o input for nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> listMenuItemsByRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Input cannot be null.");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve exigir role VIEW_MENU_ITEM quando acesso público não for permitido (cobre getRequiredRole)")
    void shouldRequireRoleWhenPublicAccessIsNotAllowed() {
        // Arrange
        Long restaurantId = 1L;
        var query = new PagedQuery<>(restaurantId, 0, 10);

        var securedUseCase = new SecuredListMenuItemsByRestaurantUseCase(
                loggedUserGateway,
                menuItemGateway,
                restaurantGateway
        );

        given(loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> securedUseCase.execute(query))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
    }


    /**
     * Subclasse apenas para forçar isPublicAccessAllowed() = false e assim
     * passar pela checagem de role (cobrindo getRequiredRole()).
     */
    private static final class SecuredListMenuItemsByRestaurantUseCase extends ListMenuItemsByRestaurantUseCase {

        SecuredListMenuItemsByRestaurantUseCase(
                LoggedUserGateway loggedUserGateway,
                MenuItemGateway menuItemGateway,
                RestaurantGateway restaurantGateway
        ) {
            super(loggedUserGateway, menuItemGateway, restaurantGateway);
        }

        @Override
        protected boolean isPublicAccessAllowed() {
            return false;
        }
    }
}