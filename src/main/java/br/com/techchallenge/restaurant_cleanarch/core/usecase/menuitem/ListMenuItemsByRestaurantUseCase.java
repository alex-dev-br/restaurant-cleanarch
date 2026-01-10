package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class ListMenuItemsByRestaurantUseCase extends UseCaseBase<PagedQuery<Long>, Page<MenuItem>> {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public ListMenuItemsByRestaurantUseCase(LoggedUserGateway loggedUserGateway, MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null.");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    protected Page<MenuItem> doExecute(PagedQuery<Long> filter) {
        restaurantGateway.findById(filter.filter()).orElseThrow(() -> new BusinessException("Restaurante not found"));
        return menuItemGateway.findByRestaurant(filter);
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return MenuItemRoles.VIEW_MENU_ITEM;
    }

    @Override
    protected boolean isPublicAccessAllowed() {
        return true;
    }
}
