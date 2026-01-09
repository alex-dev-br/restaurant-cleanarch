package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;

import java.util.Objects;

public class DeleteMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final LoggedUserGateway loggedUserGateway;

    public DeleteMenuItemUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {

        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");

        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public void execute(Long id) {

        Objects.requireNonNull(id, "id cannot be null");

        if (!loggedUserGateway.hasRole(MenuItemRoles.DELETE_MENU_ITEM)) {
            throw new OperationNotAllowedException("Você não tem permissão para deletar itens de menu.");
        }

        var restaurantId = menuItemGateway.findRestaurantIdByItemId(id)
                .orElseThrow(() -> new BusinessException("Restaurante associado não encontrado"));

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado com ID: " + restaurantId));

        // Valida se é o dono
        User currentUser = loggedUserGateway.requireCurrentUser();
        if (!restaurant.getOwner().equals(currentUser)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode deletar itens do cardápio.");
        }

        menuItemGateway.deleteById(id);
    }


}
