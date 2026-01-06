package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;

public class UpdateMenuItemUseCase {
    public UpdateMenuItemUseCase(MenuItemGateway menuItemGateway, MenuGateway menuGateway, LoggedUserGateway loggedUser) {
    }

    public void execute(UpdateMenuItemInput updateMenuItemInput) {

    }
}
