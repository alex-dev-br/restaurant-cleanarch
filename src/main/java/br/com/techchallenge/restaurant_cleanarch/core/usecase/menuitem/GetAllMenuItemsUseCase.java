package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Pagination;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;

public class GetAllMenuItemsUseCase {
    public GetAllMenuItemsUseCase(MenuItemGateway menuItemGateway, LoggedUserGateway loggedUser) {
    }

    public Pagination<MenuItemGateway> execute() {
        return null;
    }
}
