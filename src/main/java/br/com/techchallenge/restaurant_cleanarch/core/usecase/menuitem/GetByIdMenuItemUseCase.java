package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;

public class GetByIdMenuItemUseCase {
    public GetByIdMenuItemUseCase(MenuItemGateway menuItemGateway, LoggedUserGateway loggedUser) {
    }

    public MenuItem execute(Long menuItemId) {
        return null;
    }
}
