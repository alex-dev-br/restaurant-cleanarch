package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.Optional;

public class GetRestaurantByIdUseCase extends UseCaseBase<Long, Optional<Restaurant>> {

    private final RestaurantGateway restaurantGateway;

    public GetRestaurantByIdUseCase(
            LoggedUserGateway loggedUserGateway,
            RestaurantGateway restaurantGateway
    ) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.VIEW_RESTAURANT;
    }

    @Override
    protected Optional<Restaurant> doExecute(Long id) {
        // aqui id NÃO será null, pois UseCaseBase.execute já valida input != null
        return restaurantGateway.findById(id);
    }
}
