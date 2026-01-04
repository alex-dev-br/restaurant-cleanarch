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

        // Valida se restaurante existe e já carrega o objeto completo
        Restaurant restaurant = restaurantGateway.findById(input.restaurantId())
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado com ID: " + input.restaurantId()));

        // Verifica duplicata de nome no mesmo restaurante
        if (menuItemGateway.existsByNameAndRestaurant(input.name(), restaurant.getId())) {
            throw new BusinessException(
                    "Já existe um item de cardápio com o nome '%s' no restaurante '%s'."
                            .formatted(input.name().trim(), restaurant.getName())
            );
        }

        // Cria o domínio com a associação correta
        MenuItem menuItem = new MenuItem(
                null,
                input.name(),
                input.description(),
                input.price(),
                input.restaurantOnly(),
                input.photoPath(),
                restaurant  // <-- Associação forte com o restaurante existente
        );

        return menuItemGateway.save(menuItem);
    }
}
