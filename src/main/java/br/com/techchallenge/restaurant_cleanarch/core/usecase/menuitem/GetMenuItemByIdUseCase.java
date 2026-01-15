package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class GetMenuItemByIdUseCase extends UseCaseBase<Long, MenuItem> {

    private final MenuItemGateway menuItemGateway;

    public GetMenuItemByIdUseCase(MenuItemGateway menuItemGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        this.menuItemGateway = menuItemGateway;
    }

    @Override
    protected MenuItem doExecute(Long id) {
        return menuItemGateway.findById(id)
                .orElseThrow(() -> new BusinessException("Item de cardápio não encontrado"));
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return MenuItemRoles.VIEW_MENU_ITEM;
    }
}
