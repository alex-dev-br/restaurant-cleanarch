package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteMenuItemUseCase")
class DeleteMenuItemUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;

    @InjectMocks
    private DeleteMenuItemUseCase useCase;

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        given(loggedUserGateway.hasRole(MenuItemRoles.DELETE_MENU_ITEM)).willReturn(false);

        assertThatThrownBy(() -> useCase.execute(10L))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.DELETE_MENU_ITEM);
        then(loggedUserGateway).shouldHaveNoMoreInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando não encontrar restaurantId associado ao item")
    void shouldThrowBusinessExceptionWhenRestaurantIdNotFoundForItem() {
        Long itemId = 10L;

        given(loggedUserGateway.hasRole(MenuItemRoles.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante associado não encontrado");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.DELETE_MENU_ITEM);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);

        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        Long itemId = 10L;
        Long restaurantId = 99L;

        given(loggedUserGateway.hasRole(MenuItemRoles.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante não encontrado com ID: " + restaurantId);

        then(loggedUserGateway).should().hasRole(MenuItemRoles.DELETE_MENU_ITEM);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);
        then(restaurantGateway).should().findById(restaurantId);

        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve negar quando usuário não for o dono (comparando por UUID)")
    void shouldThrowOperationNotAllowedWhenUserIsNotOwner() {
        Long itemId = 10L;
        Long restaurantId = 1L;

        UUID ownerId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        given(loggedUserGateway.hasRole(MenuItemRoles.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        given(restaurant.getOwner()).willReturn(owner);
        given(owner.getId()).willReturn(ownerId);
        given(currentUser.getId()).willReturn(currentUserId);

        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode deletar itens do cardápio.");

        then(menuItemGateway).should(never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve deletar item com sucesso quando usuário for dono e tiver permissão")
    void shouldDeleteSuccessfullyWhenUserIsOwnerAndHasRole() {
        Long itemId = 10L;
        Long restaurantId = 1L;

        UUID ownerId = UUID.randomUUID();

        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        given(loggedUserGateway.hasRole(MenuItemRoles.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        given(restaurant.getOwner()).willReturn(owner);
        given(owner.getId()).willReturn(ownerId);
        given(currentUser.getId()).willReturn(ownerId);

        useCase.execute(itemId);

        then(menuItemGateway).should().deleteById(itemId);
    }
}
