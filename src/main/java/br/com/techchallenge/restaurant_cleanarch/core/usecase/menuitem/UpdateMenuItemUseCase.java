package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuItemGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class UpdateMenuItemUseCase extends UseCaseBase<UpdateMenuItemInput, MenuItem> {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public UpdateMenuItemUseCase(
            LoggedUserGateway loggedUserGateway,
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway
    ) {
        super(loggedUserGateway);
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return MenuItemRoles.UPDATE_MENU_ITEM;
    }

    @Override
    protected MenuItem doExecute(UpdateMenuItemInput input) {
        Long itemId = Objects.requireNonNull(input.id(), "id cannot be null");

        MenuItem existingItem = menuItemGateway.findById(itemId)
                .orElseThrow(() -> new BusinessException("Item de cardápio não encontrado com ID: " + itemId));

        Long restaurantId = menuItemGateway.findRestaurantIdByItemId(itemId)
                .orElseThrow(() -> new BusinessException("Restaurante associado não encontrado"));

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado"));

        // valida se é o dono
        User currentUser = loggedUserGateway.requireCurrentUser();
        var ownerId = restaurant.getOwner() != null ? restaurant.getOwner().getId() : null;
        var currentUserId = currentUser.getId();

        if (ownerId == null || currentUserId == null || !ownerId.equals(currentUserId)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode atualizar itens do cardápio.");
        }

        String newName = Objects.requireNonNull(input.name(), "name cannot be null").trim();
        String oldName = existingItem.getName() != null ? existingItem.getName().trim() : null;

        if (!newName.equals(oldName) && menuItemGateway.existsByNameAndRestaurantId(newName, restaurantId)) {
            throw new BusinessException("Já existe um item com este nome no restaurante");
        }

        MenuItem updatedItem = new MenuItem(
                existingItem.getId(),
                newName,
                input.description() != null ? input.description().trim() : null,
                input.price(),
                input.restaurantOnly(),
                input.photoPath() != null ? input.photoPath().trim() : existingItem.getPhotoPath()
        );

        return menuItemGateway.save(updatedItem, restaurantId);
    }
}
