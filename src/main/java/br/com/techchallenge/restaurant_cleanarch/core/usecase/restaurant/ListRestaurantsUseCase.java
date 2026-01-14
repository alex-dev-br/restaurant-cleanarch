package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.List;
import java.util.Objects;

public class ListRestaurantsUseCase extends UseCaseBase<ListRestaurantsUseCase.Input, List<Restaurant>> {

    public enum Input {
        INSTANCE
    }

    private final RestaurantGateway restaurantGateway;

    public ListRestaurantsUseCase(
            LoggedUserGateway loggedUserGateway,
            RestaurantGateway restaurantGateway
    ) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    // Mantém a API antiga (sem parâmetros)
    public List<Restaurant> execute() {
        return super.execute(Input.INSTANCE);
    }

    @Override
    protected List<Restaurant> doExecute(Input input) {
        // UseCaseBase já garante input != null
        return restaurantGateway.findAll();
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.VIEW_RESTAURANT;
    }
}
