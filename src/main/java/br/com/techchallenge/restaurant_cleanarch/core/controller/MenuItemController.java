package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.MenuItemPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.CreateMenuItemUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.ListMenuItemsByRestaurantUseCase;

import java.util.Objects;

public class MenuItemController {

    private final ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase;
    private final CreateMenuItemUseCase createMenuItemUseCase;

    public MenuItemController(ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase, CreateMenuItemUseCase createMenuItemUseCase) {
        Objects.requireNonNull(listMenuItemsByRestaurantUseCase, "ListMenuItemsByRestaurantUseCase cannot be null.");
        Objects.requireNonNull(createMenuItemUseCase, "CreateMenuItemUseCase cannot be null.");
        this.listMenuItemsByRestaurantUseCase = listMenuItemsByRestaurantUseCase;
        this.createMenuItemUseCase = createMenuItemUseCase;
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
}
