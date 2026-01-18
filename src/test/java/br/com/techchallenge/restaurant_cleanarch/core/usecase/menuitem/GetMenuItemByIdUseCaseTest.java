package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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
    @DisplayName("Deve lançar BusinessException quando item não existir")
    void shouldThrowBusinessExceptionWhenMenuItemNotFound() {
        // Arrange
        Long itemId = 10L;

        given(menuItemGateway.findById(itemId)).willReturn(Optional.empty());

        // Act / Assert
        var optionalResult = useCase.execute(itemId);

        assertThat(optionalResult).isEmpty();

        then(loggedUserGateway).should(never()).hasRole(MenuItemRoles.VIEW_MENU_ITEM);
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

        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(expected));

        // Act
        var optionalResult = useCase.execute(itemId);

        // Assert
        assertThat(optionalResult).isNotEmpty();
        var result = optionalResult.get();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Pizza Margherita");

        then(loggedUserGateway).should(never()).hasRole(MenuItemRoles.VIEW_MENU_ITEM);
        then(menuItemGateway).should().findById(itemId);

        then(loggedUserGateway).shouldHaveNoMoreInteractions();
        then(menuItemGateway).shouldHaveNoMoreInteractions();
    }
}
