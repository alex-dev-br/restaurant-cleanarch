package br.com.techchallenge.restaurant_cleanarch.core.usecase.user;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.UUID;

public class GetUserByIdUseCase extends UseCaseBase<UUID, User> {

    private final UserGateway userGateway;

    public GetUserByIdUseCase(UserGateway userGateway, LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(loggedUserGateway, "loggedUserGateway must not be null");
        this.userGateway = userGateway;
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserManagementRoles.VIEW_USER;
    }

    @Override
    public User doExecute(UUID id) {
        return userGateway.findById(id)
                .orElseThrow(() -> new BusinessException("User with ID " + id + " not found."));
    }
}
