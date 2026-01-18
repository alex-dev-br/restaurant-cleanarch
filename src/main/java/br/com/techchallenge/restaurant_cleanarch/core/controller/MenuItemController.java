package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.MenuItemPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.*;

import java.util.Objects;
import java.util.Optional;

public class MenuItemController {

    private final ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase;
    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;
    private final GetMenuItemByIdUseCase getMenuItemByIdUseCase;

    public MenuItemController(ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase,
                              CreateMenuItemUseCase createMenuItemUseCase,
                              UpdateMenuItemUseCase updateMenuItemUseCase,
                              DeleteMenuItemUseCase deleteMenuItemUseCase,
                              GetMenuItemByIdUseCase getMenuItemByIdUseCase) {
        Objects.requireNonNull(listMenuItemsByRestaurantUseCase, "ListMenuItemsByRestaurantUseCase cannot be null.");
        Objects.requireNonNull(createMenuItemUseCase, "CreateMenuItemUseCase cannot be null.");
        Objects.requireNonNull(updateMenuItemUseCase, "UpdateMenuItemUseCase cannot be null.");
        Objects.requireNonNull(deleteMenuItemUseCase, "DeleteMenuItemUseCase cannot be null.");
        Objects.requireNonNull(getMenuItemByIdUseCase, "GetMenuItemByIdUseCase cannot be null.");
        this.listMenuItemsByRestaurantUseCase = listMenuItemsByRestaurantUseCase;
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
        this.getMenuItemByIdUseCase = getMenuItemByIdUseCase;
    }

    public Page<MenuItemOutput> findByRestaurant(Long restaurantId, int pageNumber, int pageSize) {
        var pagedQuery = new PagedQuery<>(restaurantId, pageNumber, pageSize);
        var page = listMenuItemsByRestaurantUseCase.execute(pagedQuery);
        return page.mapItems(menuItem -> MenuItemPresenter.toOutput(menuItem, restaurantId));
    }

    public MenuItemOutput addItemInMenu(CreateMenuItemInput input) {
        var menuItem = createMenuItemUseCase.execute(input);
        return MenuItemPresenter.toOutput(menuItem, input.restaurantId());
    }

    public void updateMenuItem(UpdateMenuItemInput input) {
        updateMenuItemUseCase.execute(input);
    }

    public void deleteById(Long menuItemId) {
        deleteMenuItemUseCase.execute(menuItemId);
    }

    public Optional<MenuItemOutput> getById(Long menuItemId) {
        return getMenuItemByIdUseCase.execute(menuItemId).map(m -> MenuItemPresenter.toOutput(m, null));
    }
}
