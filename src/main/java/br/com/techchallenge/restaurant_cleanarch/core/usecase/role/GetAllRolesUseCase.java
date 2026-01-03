package br.com.techchallenge.restaurant_cleanarch.core.usecase.role;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RoleRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;

import java.util.Objects;
import java.util.Set;

public class GetAllRolesUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final RoleGateway roleGateway;

    public GetAllRolesUseCase(LoggedUserGateway loggedUserGateway, RoleGateway roleGateway) {
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        Objects.requireNonNull(roleGateway, "RoleGateway cannot be null.");
        this.loggedUserGateway = loggedUserGateway;
        this.roleGateway = roleGateway;
    }

    public Set<Role> execute() {
        if (!loggedUserGateway.hasRole(RoleRoles.VIEW_ROLE)) {
            throw new OperationNotAllowedException("The current user does not have permission to get all roles.");
        }

        return roleGateway.findAll();
    }
}
