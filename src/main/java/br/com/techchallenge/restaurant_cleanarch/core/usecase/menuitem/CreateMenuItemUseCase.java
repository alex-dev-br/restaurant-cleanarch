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
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.UUID;

public class CreateMenuItemUseCase extends UseCaseBase<CreateMenuItemInput, MenuItem> {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public CreateMenuItemUseCase(
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
        return MenuItemRoles.CREATE_MENU_ITEM;
    }

    @Override
    protected MenuItem doExecute(CreateMenuItemInput input) {
        // Base já valida input != null (Input cannot be null.)
        Long restaurantId = Objects.requireNonNull(input.restaurantId(), "restaurantId cannot be null.");

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado com ID: " + restaurantId));

        String name = Objects.requireNonNull(input.name(), "name cannot be null").trim();
        if (name.isBlank()) {
            throw new BusinessException("name cannot be blank");
        }

        String description = input.description() != null ? input.description().trim() : null;
        String photoPath = input.photoPath() != null ? input.photoPath().trim() : null;

        User currentUser = loggedUserGateway.requireCurrentUser();

        // compara por id (mais robusto que equals de instância)
        UUID ownerId = restaurant.getOwner() != null ? restaurant.getOwner().getId() : null;
        UUID currentUserId = currentUser != null ? currentUser.getId() : null;

        if (ownerId == null || currentUserId == null || !ownerId.equals(currentUserId)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode criar itens do cardápio.");
        }

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

        return menuItemGateway.save(menuItem, restaurantId);
    }
}
