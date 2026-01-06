package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Pagination;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;

public class GetAllUsersUseCase {
    public GetAllUsersUseCase(UserGateway userGateway, LoggedUserGateway loggedUser) {
    }

    public Pagination<User> execute() {
        return null;
    }
}
