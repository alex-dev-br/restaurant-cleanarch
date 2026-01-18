package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.Optional;

public class GetMenuItemByIdUseCase extends UseCaseBase<Long, Optional<MenuItem>> {

    private final MenuItemGateway menuItemGateway;

    public GetMenuItemByIdUseCase(MenuItemGateway menuItemGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        this.menuItemGateway = menuItemGateway;
    }

    @Override
    protected Optional<MenuItem> doExecute(Long id) {
        return menuItemGateway.findById(id);
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return MenuItemRoles.VIEW_MENU_ITEM;
    }
}
