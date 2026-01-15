package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.UUID;

public class DeleteMenuItemUseCase extends UseCaseBase<Long, Void> {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public DeleteMenuItemUseCase(
            LoggedUserGateway loggedUserGateway,
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway
    ) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null.");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return MenuItemRoles.DELETE_MENU_ITEM;
    }

    @Override
    protected Void doExecute(Long id) {
        Objects.requireNonNull(id, "id cannot be null");

        var restaurantId = menuItemGateway.findRestaurantIdByItemId(id)
                .orElseThrow(() -> new BusinessException("Restaurante associado não encontrado"));

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado com ID: " + restaurantId));

        User currentUser = loggedUserGateway.requireCurrentUser();

        var ownerId = restaurant.getOwner() != null ? restaurant.getOwner().getId() : null;
        var currentId = currentUser != null ? currentUser.getId() : null;

        if (ownerId == null || currentId == null || !ownerId.equals(currentId)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode deletar itens do cardápio.");
        }

        menuItemGateway.deleteById(id);
        return null;
    }
}
