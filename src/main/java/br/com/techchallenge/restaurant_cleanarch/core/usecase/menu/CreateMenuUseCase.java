package br.com.techchallenge.restaurant_cleanarch.core.usecase.menu;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Menu;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuInput;

public class CreateMenuUseCase {
    public CreateMenuUseCase(MenuGateway menuGateway, RestaurantGateway restaurantGateway, LoggedUserGateway loggedUser) {
    }

    public Menu execute(CreateMenuInput createMenuInput) {
        return null;
    }
}
