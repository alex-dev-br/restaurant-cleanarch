package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;

import java.util.Objects;

public class UpdateMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final LoggedUserGateway loggedUserGateway;

    public UpdateMenuItemUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {

        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");

        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public MenuItem execute(UpdateMenuItemInput input) {
        Objects.requireNonNull(input, "UpdateMenuItemInput cannot be null");

        if (!loggedUserGateway.hasRole(MenuItemRoles.UPDATE_MENU_ITEM)) {
            throw new OperationNotAllowedException("Você não tem permissão para atualizar itens de menu.");
        }

        Long itemId = Objects.requireNonNull(input.id(), "id cannot be null");

        MenuItem existingItem = menuItemGateway.findById(itemId)
                .orElseThrow(() -> new BusinessException("Item de cardápio não encontrado com ID: " + itemId));

        Long restaurantId = menuItemGateway.findRestaurantIdByItemId(itemId)
                .orElseThrow(() -> new BusinessException("Restaurante associado não encontrado"));

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado"));

        // Valida se é o dono
        User currentUser = loggedUserGateway.requireCurrentUser();
        if (!restaurant.getOwner().equals(currentUser)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode atualizar itens do cardápio.");
        }

        String newName = Objects.requireNonNull(input.name(), "name cannot be null").trim();
        String oldName = existingItem.getName() != null ? existingItem.getName().trim() : null;

        // Verifica duplicata de nome no mesmo restaurante (se nome mudou)
        if (!newName.equals(oldName)
                && menuItemGateway.existsByNameAndRestaurantId(newName, restaurantId)) {
            throw new BusinessException("Já existe um item com este nome no restaurante");
        }

        MenuItem updatedItem = new MenuItem(
                existingItem.getId(),
                newName,
                input.description() != null ? input.description().trim() : null,
                input.price(),
                input.restaurantOnly(),
                input.photoPath() != null ? input.photoPath().trim() : null
        );

        return menuItemGateway.save(updatedItem, restaurantId);
    }
}
