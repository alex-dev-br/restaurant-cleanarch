package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateMenuItemUseCase")
class CreateMenuItemUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;

    @InjectMocks
    private CreateMenuItemUseCase useCase;

    @Captor
    private ArgumentCaptor<MenuItem> menuItemCaptor;

    private MenuItemBuilder builder() {
        return new MenuItemBuilder()
                .withName("Pizza")
                .withDescription("Pizza clássica")
                .withPrice(new BigDecimal("30"))
                .withRestaurantOnly(false)
                .withPhotoPath("/photos/pizza.jpg");
    }

    /**
     * Helper: configura o fluxo para passar por:
     * - role ok
     * - restaurante existe
     * - currentUser existe
     * - ownerId == currentUserId
     *
     * IMPORTANTE: chame este helper somente nos testes que realmente precisam
     * passar pela validação de owner, senão o Mockito vai acusar stubs não usados.
     */
    private Restaurant arrangeAuthorizedOwnerFlow(Long restaurantId, UUID ownerId) {
        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        given(restaurant.getOwner()).willReturn(owner);
        given(owner.getId()).willReturn(ownerId);
        given(currentUser.getId()).willReturn(ownerId);

        return restaurant;
    }

    @Test
    @DisplayName("Deve criar item com sucesso quando usuário tem role e é o dono (owner por UUID)")
    void shouldCreateSuccessfullyWhenUserHasRoleAndIsOwner() {
        // Arrange
        Long restaurantId = 10L;
        UUID ownerId = UUID.randomUUID();

        arrangeAuthorizedOwnerFlow(restaurantId, ownerId);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        MenuItem saved = new MenuItem(
                123L, "Pizza", "Pizza clássica", new BigDecimal("30"), false, "/photos/pizza.jpg"
        );
        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId))).willReturn(saved);

        CreateMenuItemInput input = builder()
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act
        MenuItem result = useCase.execute(input);

        // Assert
        assertThat(result.getId()).isEqualTo(123L);

        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem captured = menuItemCaptor.getValue();
        assertThat(captured.getId()).isNull();
        assertThat(captured.getName()).isEqualTo("Pizza");
        assertThat(captured.getPhotoPath()).isEqualTo("/photos/pizza.jpg");

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(loggedUserGateway).should().hasRole(MenuItemRoles.CREATE_MENU_ITEM);
    }

    @Test
    @DisplayName("Deve negar quando não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenNoRole() {
        // Arrange
        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(false);

        CreateMenuItemInput input = builder()
                .withRestaurantId(1L)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should().hasRole(MenuItemRoles.CREATE_MENU_ITEM);
        then(loggedUserGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 99L;

        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        CreateMenuItemInput input = builder()
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante não encontrado com ID: " + restaurantId);

        then(loggedUserGateway).should().hasRole(MenuItemRoles.CREATE_MENU_ITEM);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando usuário não é o dono (ownerId != currentUserId)")
    void shouldThrowOperationNotAllowedWhenUserIsNotOwner() {
        // Arrange
        Long restaurantId = 10L;

        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        UUID ownerId = UUID.randomUUID();
        UUID currentId = UUID.randomUUID();

        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        given(restaurant.getOwner()).willReturn(owner);
        given(owner.getId()).willReturn(ownerId);
        given(currentUser.getId()).willReturn(currentId);

        CreateMenuItemInput input = builder()
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode criar itens do cardápio.");

        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando restaurante não tem owner (ownerId = null)")
    void shouldThrowOperationNotAllowedWhenRestaurantOwnerIsNull() {
        // Arrange
        Long restaurantId = 10L;

        Restaurant restaurant = mock(Restaurant.class);
        User currentUser = mock(User.class);

        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        given(restaurant.getOwner()).willReturn(null); // <- cobre ownerId == null
        given(currentUser.getId()).willReturn(UUID.randomUUID());

        CreateMenuItemInput input = builder()
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode criar itens do cardápio.");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(menuItemGateway).should(never()).save(any(), any());
    }


    @Test
    @DisplayName("Deve lançar BusinessException quando name for blank")
    void shouldThrowBusinessExceptionWhenNameBlank() {
        // Arrange
        Long restaurantId = 10L;

        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(mock(Restaurant.class)));

        // NOTE: sem stubs de owner/user, porque falha antes.
        CreateMenuItemInput input = builder()
                .withName("   ")
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("name cannot be blank");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando name é nulo")
    void shouldThrowNullPointerExceptionWhenNameIsNull() {
        // Arrange
        Long restaurantId = 1L;

        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(mock(Restaurant.class)));

        // NOTE: sem stubs de owner/user, porque explode em Objects.requireNonNull(input.name()) antes.
        CreateMenuItemInput input = builder()
                .withName(null)
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name cannot be null");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando nome duplicado no restaurante")
    void shouldThrowBusinessExceptionWhenDuplicatedNameInRestaurant() {
        // Arrange
        Long restaurantId = 10L;
        UUID ownerId = UUID.randomUUID();

        Restaurant restaurant = arrangeAuthorizedOwnerFlow(restaurantId, ownerId);

        given(restaurant.getName()).willReturn("Rest X");
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(true);

        CreateMenuItemInput input = builder()
                .withName("  Pizza  ")
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um item de cardápio com o nome 'Pizza' no restaurante 'Rest X'.");

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(menuItemGateway).should(never()).save(any(), any());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando photoPath é nulo (MenuItem exige não-nulo)")
    void shouldThrowNullPointerExceptionWhenPhotoPathIsNull() {
        // Arrange
        Long restaurantId = 10L;
        UUID ownerId = UUID.randomUUID();

        arrangeAuthorizedOwnerFlow(restaurantId, ownerId);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        CreateMenuItemInput input = builder()
                .withPhotoPath(null)
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O caminho da foto não pode ser nulo.");

        then(menuItemGateway).should(never()).save(any(), any());
    }

    @Test
    @DisplayName("Deve negar quando usuário atual for nulo (currentUserId = null)")
    void shouldThrowOperationNotAllowedWhenCurrentUserIsNull() {
        // Arrange
        Long restaurantId = 10L;

        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);

        given(loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(null); // <- cobre currentUserId == null

        given(restaurant.getOwner()).willReturn(owner);
        given(owner.getId()).willReturn(UUID.randomUUID());

        CreateMenuItemInput input = builder()
                .withRestaurantId(restaurantId)
                .buildCreateInput();

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode criar itens do cardápio.");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(menuItemGateway).should(never()).save(any(), any());
    }

    @Test
    @DisplayName("Deve permitir description nula (description = null)")
    void shouldAllowNullDescription() {
        // Arrange
        Long restaurantId = 10L;
        UUID ownerId = UUID.randomUUID();

        arrangeAuthorizedOwnerFlow(restaurantId, ownerId);

        // precisa passar pela validação de duplicidade
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        // retorno do save (pode ser qualquer objeto)
        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId)))
                .willAnswer(inv -> inv.getArgument(0));

        CreateMenuItemInput input = builder()
                .withRestaurantId(restaurantId)
                .withDescription(null) // <- cobre o ramo input.description() == null
                .buildCreateInput();

        // Act
        useCase.execute(input);

        // Assert
        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem captured = menuItemCaptor.getValue();

        assertThat(captured.getDescription()).isNull(); // <- valida o ramo do ternário

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(loggedUserGateway).should().hasRole(MenuItemRoles.CREATE_MENU_ITEM);
    }


}