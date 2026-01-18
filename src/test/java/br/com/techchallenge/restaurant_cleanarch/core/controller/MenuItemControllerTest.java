package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.util.MenuItemBuilder;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.CreateMenuItemUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.ListMenuItemsByRestaurantUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.UpdateMenuItemUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
@DisplayName("Testes para MenuItemController")
class MenuItemControllerTest {

    @Mock
    private ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase;

    @Mock
    private CreateMenuItemUseCase createMenuItemUseCase;

    @Mock
    private UpdateMenuItemUseCase updateMenuItemUseCase;

    @InjectMocks
    private MenuItemController menuItemController;

    @Captor
    private ArgumentCaptor<PagedQuery<Long>> pagedQueryCaptor;

    @Test
    @DisplayName("Deve buscar por restaurante e retornar página de MenuItemOutput")
    void shouldFindByRestaurantAndReturnPageOfMenuItemOutput() {
        // Arrange
        Long restaurantId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        MenuItem menuItem = new MenuItemBuilder().build();
        var menuItemPage = new Page<>(pageNumber, pageSize, 1L, List.of(menuItem));

        given(listMenuItemsByRestaurantUseCase.execute(any())).willReturn(menuItemPage);

        // Act
        Page<MenuItemOutput> result = menuItemController.findByRestaurant(restaurantId, pageNumber, pageSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).hasSize(1);

        assertThat(result.content().getFirst().name()).isEqualTo(menuItem.getName());

        then(listMenuItemsByRestaurantUseCase).should().execute(pagedQueryCaptor.capture());
        PagedQuery<Long> capturedQuery = pagedQueryCaptor.getValue();
        assertThat(capturedQuery.filter()).isEqualTo(restaurantId);
        assertThat(capturedQuery.pageNumber()).isEqualTo(pageNumber);
        assertThat(capturedQuery.pageSize()).isEqualTo(pageSize);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum item de menu for encontrado")
    void shouldReturnEmptyPageWhenNoMenuItemFound() {
        // Arrange
        Long restaurantId = 1L;
        int pageNumber = 0;
        int pageSize = 10;

        Page<MenuItem> emptyPage = new Page<>(pageNumber, pageSize, 0L, List.of());
        given(listMenuItemsByRestaurantUseCase.execute(any())).willReturn(emptyPage);

        // Act
        Page<MenuItemOutput> result = menuItemController.findByRestaurant(restaurantId, pageNumber, pageSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero(); // totalElements=0 => totalPages=0
        assertThat(result.content()).isEmpty();

        then(listMenuItemsByRestaurantUseCase).should().execute(any());
    }

    @Test
    @DisplayName("Deve criar item de menu com sucesso")
    void deveCriarItemDeMenuComSucesso() {
        var menuItemBuilder = new MenuItemBuilder();
        MenuItem expectedMenuItem = menuItemBuilder.build();
        var createMenuItemInput = menuItemBuilder.buildCreateInput();

        given(createMenuItemUseCase.execute(any())).willReturn(expectedMenuItem);

        MenuItemOutput result = menuItemController.addItemInMenu(createMenuItemInput);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedMenuItem.getId());
        assertThat(result.name()).isEqualTo(expectedMenuItem.getName());
        assertThat(result.description()).isEqualTo(expectedMenuItem.getDescription());
        assertThat(result.price()).isEqualTo(expectedMenuItem.getPrice());
        assertThat(result.restaurantOnly()).isEqualTo(expectedMenuItem.getRestaurantOnly());
        assertThat(result.photoPath()).isEqualTo(expectedMenuItem.getPhotoPath());
        assertThat(result.restaurantId()).isEqualTo(createMenuItemInput.restaurantId());

        then(createMenuItemUseCase).should().execute(any(CreateMenuItemInput.class));
    }


    @Test
    @DisplayName("Deve alterar sucesso")
    void deveAlterarItemDeMenuComSucesso() {
        var menuItemBuilder = new MenuItemBuilder();
        var updateMenuItemInput = menuItemBuilder.buildUpdateInput();
        menuItemController.updateMenuItem(updateMenuItemInput);
        then(updateMenuItemUseCase).should().execute(any(UpdateMenuItemInput.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com ListMenuItemsByRestaurantUseCase nulo")
    void shouldThrowExceptionWhenListMenuItemsByRestaurantUseCaseIsNull() {
        assertThatThrownBy(() -> new MenuItemController(null, createMenuItemUseCase, updateMenuItemUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ListMenuItemsByRestaurantUseCase cannot be null.");

        then(listMenuItemsByRestaurantUseCase).should(never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com CreateMenuItemUseCase nulo")
    void shouldThrowExceptionWhenCreateMenuItemUseCaseIsNull() {
        assertThatThrownBy(() -> new MenuItemController(listMenuItemsByRestaurantUseCase, null, updateMenuItemUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateMenuItemUseCase cannot be null.");

        then(listMenuItemsByRestaurantUseCase).should(never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao instanciar controller com UpdateMenuItemUseCase nulo")
    void shouldThrowExceptionWhenUpdateMenuItemUseCaseIsNull() {
        assertThatThrownBy(() -> new MenuItemController(listMenuItemsByRestaurantUseCase, createMenuItemUseCase, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateMenuItemUseCase cannot be null.");

        then(listMenuItemsByRestaurantUseCase).should(never()).execute(any());
    }
}
