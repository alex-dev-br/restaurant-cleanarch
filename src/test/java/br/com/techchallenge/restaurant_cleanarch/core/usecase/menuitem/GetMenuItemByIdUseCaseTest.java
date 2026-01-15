package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetMenuItemByIdUseCase (UseCaseBase)")
class GetMenuItemByIdUseCaseTest {

    @Mock private MenuItemGateway menuItemGateway;
    @Mock private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetMenuItemByIdUseCase useCase;

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Arrange
        // (nada)

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        // Arrange
        Long itemId = 10L;
        given(loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(loggedUserGateway).shouldHaveNoMoreInteractions();

        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando item não existir")
    void shouldThrowBusinessExceptionWhenMenuItemNotFound() {
        // Arrange
        Long itemId = 10L;

        given(loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Item de cardápio não encontrado");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(menuItemGateway).should().findById(itemId);

        then(loggedUserGateway).shouldHaveNoMoreInteractions();
        then(menuItemGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Deve retornar item quando existir e usuário tiver permissão")
    void shouldReturnMenuItemWhenFoundAndUserHasRole() {
        // Arrange
        Long itemId = 10L;

        MenuItem expected = new MenuItemBuilder()
                .withDefaults()
                .withId(itemId)
                .withName("Pizza Margherita")
                .build();

        given(loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(expected));

        // Act
        MenuItem result = useCase.execute(itemId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Pizza Margherita");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(menuItemGateway).should().findById(itemId);

        then(loggedUserGateway).shouldHaveNoMoreInteractions();
        then(menuItemGateway).shouldHaveNoMoreInteractions();
    }
}
