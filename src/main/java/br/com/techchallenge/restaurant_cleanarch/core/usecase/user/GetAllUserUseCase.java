package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.*;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;

import java.util.*;

public class GetAllUserUseCase {

    private final UserGateway userGateway;
    private final LoggedUserGateway loggedUserGateway;

    public GetAllUserUseCase(UserGateway userGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(userGateway, "userGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "loggedUserGateway cannot be null");
        this.userGateway = userGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public List<User> execute() {
        if (!loggedUserGateway.hasRole(UserManagementRoles.VIEW_USER)) {
            throw new OperationNotAllowedException("The current user does not have permission to view users.");
        }

        return userGateway.findAll();
    }
}
