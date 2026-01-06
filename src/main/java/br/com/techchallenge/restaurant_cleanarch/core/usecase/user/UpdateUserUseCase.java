package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserInput;

public class UpdateUserUseCase {
    public UpdateUserUseCase(UserGateway userGateway, UserTypeGateway userTypeGateway, LoggedUserGateway loggedUser) {
    }

    public void execute(UpdateUserInput updateUserInput) {

    }
}
