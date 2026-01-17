package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseWithoutInput;

import java.util.List;
import java.util.Objects;

public class ListUsersUseCase extends UseCaseWithoutInput<List<User>> {

    private final UserGateway userGateway;

    public ListUsersUseCase(UserGateway userGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(loggedUserGateway, "loggedUserGateway cannot be null");
        this.userGateway = userGateway;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserManagementRoles.VIEW_USER;
    }

    @Override
    public List<User> doExecute() {
        return userGateway.findAll();
    }
}
