package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;

import java.util.Objects;

public class GetByIdRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;
    private final LoggedUserGateway loggedUserGateway;

    public GetByIdRestaurantUseCase(RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        this.restaurantGateway = restaurantGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public Restaurant execute(Long id) {
        Objects.requireNonNull(id, "Restaurant Id cannot be null.");

        if (!loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT))
            throw new OperationNotAllowedException("The current user does not have permission to update restaurants.");

        return restaurantGateway
                .findById(id)
                .orElseThrow(() -> new BusinessException("Restaurant not found."));
    }
}
