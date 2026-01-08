package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;

import java.util.*;

public class GetAllMenuItemsByRestaurantUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final LoggedUserGateway loggedUserGateway;

    public GetAllMenuItemsByRestaurantUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");

        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public List<MenuItem> execute(Long restaurantId) {
        if (!loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)) {
            throw new OperationNotAllowedException("Você não tem permissão para visualizar itens de menu.");
        }

        restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado."));

        return menuItemGateway.findByRestaurantId(restaurantId);
    }
}
