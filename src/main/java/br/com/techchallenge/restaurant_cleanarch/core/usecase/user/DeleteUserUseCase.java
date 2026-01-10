package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;

import java.util.*;

public class DeleteUserUseCase {

    private final UserGateway userGateway;
    private final LoggedUserGateway loggedUserGateway;

    public DeleteUserUseCase(UserGateway userGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(userGateway, "userGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "loggedUserGateway cannot be null");

        this.userGateway = userGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public void execute(UUID id) {
        Objects.requireNonNull(id, "id must not be null");

        if (!loggedUserGateway.hasRole(UserManagementRoles.DELETE_USER)) {
            throw new OperationNotAllowedException("The current user does not have permission to delete users.");
        }

        userGateway.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));

        userGateway.deleteById(id);
    }
}
