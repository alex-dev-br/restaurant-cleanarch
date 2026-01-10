package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;



import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;

import java.util.*;

public class GetByIdUserUseCase {

    private final UserGateway userGateway;
    private final LoggedUserGateway loggedUserGateway;

    public GetByIdUserUseCase(UserGateway userGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(userGateway, "userGateway must not be null");
        Objects.requireNonNull(loggedUserGateway, "loggedUserGateway must not be null");

        this.userGateway = userGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public User execute(UUID id) {
        Objects.requireNonNull(id, "id must not be null");

        if (!loggedUserGateway.hasRole(UserManagementRoles.VIEW_USER)) {
            throw new OperationNotAllowedException("The current user does not have permission to view users.");
        }

        return userGateway.findById(id)
                .orElseThrow(() -> new BusinessException("User with ID " + id + " not found."));
    }
}
