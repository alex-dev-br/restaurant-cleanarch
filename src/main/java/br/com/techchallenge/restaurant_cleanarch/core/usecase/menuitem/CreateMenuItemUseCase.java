package br.com.techchallenge.restaurant_cleanarch.core.usecase.menuitem;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;

import java.util.Objects;

public class CreateMenuItemUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final LoggedUserGateway loggedUserGateway;

    public CreateMenuItemUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public MenuItem execute(CreateMenuItemInput input) {
        Objects.requireNonNull(input, "CreateMenuItemInput cannot be null");

        if (!loggedUserGateway.hasRole(MenuItemRoles.CREATE_MENU_ITEM)) {
            throw new OperationNotAllowedException("Você não tem permissão para criar itens de menu.");
        }

        Long restaurantId = Objects.requireNonNull(input.restaurantId(), "restaurantId cannot be null");

        // Valida se restaurante existe (você usa o nome no erro de duplicidade)
        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado com ID: " + restaurantId));

        String name = Objects.requireNonNull(input.name(), "name cannot be null").trim();
        String description = input.description() != null ? input.description().trim() : null;
        String photoPath = input.photoPath() != null ? input.photoPath().trim() : null;

        // Verifica duplicata de nome no mesmo restaurante
        if (menuItemGateway.existsByNameAndRestaurantId(name, restaurantId)) {
            throw new BusinessException(
                    "Já existe um item de cardápio com o nome '%s' no restaurante '%s'."
                            .formatted(name, restaurant.getName())
            );
        }

        MenuItem menuItem = new MenuItem(
                null,
                name,
                description,
                input.price(),
                input.restaurantOnly(),
                photoPath
        );

        // passar restaurantId para o gateway
        return menuItemGateway.save(menuItem, restaurantId);
    }
}
