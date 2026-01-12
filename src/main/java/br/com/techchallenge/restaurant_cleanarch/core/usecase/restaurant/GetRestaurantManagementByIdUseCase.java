package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.*;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class GetRestaurantManagementByIdUseCase extends UseCaseBase<Long, Restaurant> {

    private final RestaurantGateway restaurantGateway;

    public GetRestaurantManagementByIdUseCase(RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");;
    }

    @Override
    protected Restaurant doExecute(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");

        Restaurant restaurant = restaurantGateway.findByIdWithManagement(id)
                .orElseThrow(() -> new BusinessException("Restaurant not found."));

        User currentUser = loggedUserGateway.requireCurrentUser();

        boolean isOwner = restaurant.getOwner().equals(currentUser);
        boolean isEmployee = restaurant.getEmployees().contains(currentUser);

        // Owner or Employee podem ver os detalhes de gestão do restaurante
        if (!isOwner && !isEmployee) {
            throw new OperationNotAllowedException("Access denied. User is neither owner nor employee of the restaurant.");
        }

        return restaurant;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.VIEW_RESTAURANT_MANAGEMENT;
    }

}
