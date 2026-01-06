package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;

public class CreateUserUseCase {
    public CreateUserUseCase(UserGateway userGateway, UserTypeGateway userTypeGateway, LoggedUserGateway loggedUser) {

    }

    public User execute(CreateUserInput createUserInput) {
        return null;
    }
}
