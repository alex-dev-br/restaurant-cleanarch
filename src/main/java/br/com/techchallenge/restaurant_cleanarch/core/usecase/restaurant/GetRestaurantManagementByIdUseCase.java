package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.UUID;

public class GetRestaurantManagementByIdUseCase extends UseCaseBase<Long, Restaurant> {

    private final RestaurantGateway restaurantGateway;

    public GetRestaurantManagementByIdUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT;
    }

    @Override
    protected Restaurant doExecute(Long id) {
        // id não chega null aqui se usar execute(), pois UseCaseBase valida input != null.

        Restaurant restaurant = restaurantGateway.findByIdWithManagement(id)
                .orElseThrow(() -> new BusinessException("Restaurant not found."));

        User currentUser = loggedUserGateway.requireCurrentUser();
        UUID currentUserId = currentUser.getId();

        boolean isOwner = restaurant.getOwner() != null
                && restaurant.getOwner().getId() != null
                && restaurant.getOwner().getId().equals(currentUserId);

        boolean isEmployee = restaurant.getEmployees() != null
                && restaurant.getEmployees().stream()
                .map(User::getId)
                .anyMatch(currentUserId::equals);

        if (!isOwner && !isEmployee) {
            throw new OperationNotAllowedException(
                    "Access denied. User is neither owner nor employee of the restaurant."
            );
        }

        return restaurant;
    }
}
