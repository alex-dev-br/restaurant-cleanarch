package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class ListRestaurantsByCuisineTypeUseCase extends UseCaseBase<PagedQuery<String>, Page<Restaurant>> {

    private final RestaurantGateway restaurantGateway;

    public ListRestaurantsByCuisineTypeUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    protected Page<Restaurant> doExecute(PagedQuery<String> query) {
        Objects.requireNonNull(query, "PagedQuery cannot be null.");
        return restaurantGateway.findByCuisineType(query);
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.VIEW_RESTAURANT;
    }

    @Override
    protected boolean isPublicAccessAllowed() {
        return true;
    }
}
