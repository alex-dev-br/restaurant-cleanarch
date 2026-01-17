package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.ResourceNotFoundException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseWithoutOutput;

import java.util.Objects;
import java.util.UUID;

public class DeleteUserUseCase extends UseCaseWithoutOutput<UUID> {

    private final UserGateway userGateway;

    public DeleteUserUseCase(UserGateway userGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(userGateway, "userGateway cannot be null");
        this.userGateway = userGateway;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserManagementRoles.DELETE_USER;
    }

    @Override
    public void doExecute(UUID id) {
        userGateway.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
        userGateway.deleteById(id);
    }
}
