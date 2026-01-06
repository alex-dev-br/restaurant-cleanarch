package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;

import java.util.UUID;

public class GetByIdUser {
    public GetByIdUser(UserGateway userGateway, LoggedUserGateway loggedUser) {
    }

    public User execute(UUID uuid) {
        return null;
    }
}
