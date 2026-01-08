package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;

import java.util.Objects;

public class GetByIdMenuItemUseCase {

    private MenuItemGateway menuItemGateway;
    private LoggedUserGateway loggedUserGateway;

    public GetByIdMenuItemUseCase(MenuItemGateway menuItemGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");

        this.menuItemGateway = menuItemGateway;
        this.loggedUserGateway = loggedUserGateway;
    }


    public MenuItem execute(Long id) {
        if (!loggedUserGateway.hasRole(MenuItemRoles.VIEW_MENU_ITEM)) {
            throw new OperationNotAllowedException("Você não tem permissão para visualizar itens de menu.");
        }

        return menuItemGateway.findById(id)
                .orElseThrow(() -> new BusinessException("Item de cardápio não encontrado"));
    }
}
