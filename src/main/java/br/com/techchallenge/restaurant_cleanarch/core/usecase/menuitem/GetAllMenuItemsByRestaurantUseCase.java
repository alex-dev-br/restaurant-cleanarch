package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.List;
import java.util.Objects;

public class GetAllMenuItemsByRestaurantUseCase extends UseCaseBase<Long, List<MenuItem>> {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public GetAllMenuItemsByRestaurantUseCase(
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        super(loggedUserGateway);
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
    }

    @Override
    protected List<MenuItem> doExecute(Long restaurantId) {
        // aqui input já é non-null e role já foi verificada pelo UseCaseBase

        restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado."));

        return menuItemGateway.findByRestaurantId(restaurantId);
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return MenuItemRoles.VIEW_MENU_ITEM;
    }
}
