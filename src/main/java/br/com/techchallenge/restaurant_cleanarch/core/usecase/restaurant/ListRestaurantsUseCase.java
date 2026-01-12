package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;

import java.util.List;
import java.util.Objects;

public class ListRestaurantsUseCase {
    private final RestaurantGateway restaurantGateway;
    private final LoggedUserGateway loggedUserGateway;

    public ListRestaurantsUseCase(RestaurantGateway restaurantGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        this.restaurantGateway = restaurantGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public List<Restaurant> execute() {
        if (!loggedUserGateway.hasRole(RestaurantRoles.VIEW_RESTAURANT))
            throw new OperationNotAllowedException("The current user does not have permission to view restaurants.");

        return restaurantGateway.findAll();
    }

}
