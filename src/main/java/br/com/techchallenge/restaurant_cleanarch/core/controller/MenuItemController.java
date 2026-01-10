package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.MenuItemPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem.ListMenuItemsByRestaurantUseCase;

import java.util.Objects;

public class MenuItemController {

    private final ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase;

    public MenuItemController(ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase) {
        Objects.requireNonNull(listMenuItemsByRestaurantUseCase, "ListMenuItemsByRestaurantUseCase cannot be null.");
        this.listMenuItemsByRestaurantUseCase = listMenuItemsByRestaurantUseCase;
    }

    public Page<MenuItemOutput> findByRestaurant(Long restaurantId, int pageNumber, int pageSize) {
        var pagedQuery = new PagedQuery<>(restaurantId, pageNumber, pageSize);
        var page = listMenuItemsByRestaurantUseCase.execute(pagedQuery);
        return page.mapItems(menuItem -> MenuItemPresenter.toOutput(menuItem, restaurantId));
    }
}
