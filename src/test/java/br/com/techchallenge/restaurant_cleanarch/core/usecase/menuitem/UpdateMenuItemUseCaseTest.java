package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.RestaurantBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.UserBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateMenuItemUseCase")
class UpdateMenuItemUseCaseTest {

    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private LoggedUserGateway loggedUserGateway;

    @Captor
    private ArgumentCaptor<MenuItem> menuItemCaptor;

    @InjectMocks
    private UpdateMenuItemUseCase useCase;

    @Test
    @DisplayName("Deve atualizar item de menu com sucesso")
    void shouldUpdateMenuItemSuccessfully() {
        // Arrange
        Long itemId = 10L;
        Long restaurantId = 5L;

        UUID ownerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User owner = new UserBuilder()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withOwner(owner)
                .build();

        MenuItem existingItem = new MenuItemBuilder()
                .withId(itemId)
                .withName("Old Name")
                .withDescription("Old desc")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = new MenuItemBuilder()
                .withId(itemId)
                .withName("  Pizza  ")
                .withDescription("  Nova descrição  ")
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("  /photos/pizza.jpg  ")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItemRoles.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId)))
                .willAnswer(invocation -> invocation.getArgument(0, MenuItem.class));

        // Act
        MenuItem result = useCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);

        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem toSave = menuItemCaptor.getValue();

        assertThat(toSave.getId()).isEqualTo(itemId);
        assertThat(toSave.getName()).isEqualTo("Pizza");
        assertThat(toSave.getDescription()).isEqualTo("Nova descrição");
        assertThat(toSave.getPrice()).isEqualByComparingTo("25.00");
        assertThat(toSave.getRestaurantOnly()).isTrue();
        assertThat(toSave.getPhotoPath()).isEqualTo("/photos/pizza.jpg");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.UPDATE_MENU_ITEM);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando não tiver permissão")
    void shouldThrowOperationNotAllowedWhenNoPermission() {
        // Arrange
        UpdateMenuItemInput input = new MenuItemBuilder().withId(10L).buildUpdateInput();
        given(loggedUserGateway.hasRole(MenuItemRoles.UPDATE_MENU_ITEM)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("does not have permission");

        then(loggedUserGateway).should().hasRole(MenuItemRoles.UPDATE_MENU_ITEM);
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando item não existir")
    void shouldThrowBusinessExceptionWhenMenuItemNotFound() {
        // Arrange
        Long itemId = 10L;
        UpdateMenuItemInput input = new MenuItemBuilder().withId(itemId).buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItemRoles.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Item de cardápio não encontrado com ID: " + itemId);

        then(menuItemGateway).should().findById(itemId);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).save(any(), any());
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não for o dono")
    void shouldThrowOperationNotAllowedWhenCurrentUserIsNotOwner() {
        // Arrange
        Long itemId = 10L;
        Long restaurantId = 5L;

        UUID ownerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID otherId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        User owner = new UserBuilder().withId(ownerId).withRole(UserRoles.RESTAURANT_OWNER).build();
        User otherUser = new UserBuilder().withId(otherId).withRole(UserRoles.RESTAURANT_OWNER).build();

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withOwner(owner)
                .build();

        MenuItem existingItem = new MenuItemBuilder()
                .withId(itemId)
                .withName("Old Name")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = new MenuItemBuilder()
                .withId(itemId)
                .withName("Pizza")
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItemRoles.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(otherUser);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("Apenas o dono do restaurante pode atualizar itens do cardápio.");

        then(menuItemGateway).should(times(1)).findById(itemId);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);
        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should(never()).save(any(), any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando já existir item com o mesmo nome no restaurante")
    void shouldThrowBusinessExceptionWhenDuplicateName() {
        // Arrange
        Long itemId = 10L;
        Long restaurantId = 5L;

        UUID ownerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User owner = new UserBuilder().withId(ownerId).withRole(UserRoles.RESTAURANT_OWNER).build();

        Restaurant restaurant = new RestaurantBuilder()
                .withId(restaurantId)
                .withOwner(owner)
                .build();

        MenuItem existingItem = new MenuItemBuilder()
                .withId(itemId)
                .withName("Old Name")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = new MenuItemBuilder()
                .withId(itemId)
                .withName("Pizza") // mudou
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItemRoles.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um item com este nome no restaurante");

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(menuItemGateway).should(never()).save(any(), any());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
