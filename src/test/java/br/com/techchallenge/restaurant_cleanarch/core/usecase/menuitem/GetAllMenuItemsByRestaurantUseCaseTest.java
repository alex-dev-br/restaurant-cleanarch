package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetAllMenuItemsByRestaurantUseCase")
class GetAllMenuItemsByRestaurantUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;

    @InjectMocks
    private GetAllMenuItemsByRestaurantUseCase useCase;

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        // Arrange
        given(loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(1L))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(loggedUserGateway).shouldHaveNoMoreInteractions();

        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 10L;

        given(loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante não encontrado.");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(restaurantGateway).should().findById(restaurantId);

        then(menuItemGateway).should(never()).findByRestaurantId(anyLong());
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve retornar lista de itens quando restaurante existir e usuário tiver permissão")
    void shouldReturnMenuItemsWhenRestaurantExistsAndUserHasRole() {
        // Arrange
        Long restaurantId = 10L;

        Restaurant restaurant = new RestaurantBuilder()
                .withDefaults()
                .withId(restaurantId)
                .build();

        List<MenuItem> expected = List.of(
                new MenuItemBuilder().withDefaults().withName("Pizza").build(),
                new MenuItemBuilder().withDefaults().withName("Lasanha").build()
        );

        given(loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurantId(restaurantId)).willReturn(expected);

        // Act
        List<MenuItem> result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MenuItem::getName)
                .containsExactlyInAnyOrder("Pizza", "Lasanha");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurantId(restaurantId);

        then(loggedUserGateway).shouldHaveNoMoreInteractions();
        then(restaurantGateway).shouldHaveNoMoreInteractions();
        then(menuItemGateway).shouldHaveNoMoreInteractions();
    }
}
