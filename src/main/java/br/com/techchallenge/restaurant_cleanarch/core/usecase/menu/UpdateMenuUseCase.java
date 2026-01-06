package br.com.techchallenge.restaurant_cleanarch.core.usecase.menu;

import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.MenuGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuInput;

public class UpdateMenuUseCase {
    public UpdateMenuUseCase(MenuGateway menuGateway, RestaurantGateway restaurantGateway, LoggedUserGateway loggedUser) {
    }

    public void execute(UpdateMenuInput updateMenuInput) {

    }
}
