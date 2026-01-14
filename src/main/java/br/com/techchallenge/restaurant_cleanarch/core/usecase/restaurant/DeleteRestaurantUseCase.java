package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class DeleteRestaurantUseCase extends UseCaseBase<Long, Void> {

    private final RestaurantGateway restaurantGateway;

    public DeleteRestaurantUseCase(RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    protected Void doExecute(Long id) {
        var restaurant = restaurantGateway.findById(id).orElseThrow(() -> new BusinessException("Restaurant not found."));

        User currentUser = loggedUserGateway.requireCurrentUser();
        if (!restaurant.canBeManagedBy(currentUser)) {
            throw new OperationNotAllowedException();
        }

        restaurantGateway.delete(id);
        return null;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.DELETE_RESTAURANT;
    }
}
