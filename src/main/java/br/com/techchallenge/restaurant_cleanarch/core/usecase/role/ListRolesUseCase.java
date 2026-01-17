package br.com.techchallenge.restaurant_cleanarch.core.usecase.role;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RoleRoles;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseWithoutInput;

import java.util.Objects;
import java.util.Set;

public class ListRolesUseCase extends UseCaseWithoutInput<Set<Role>> {

    private final RoleGateway roleGateway;

    public ListRolesUseCase(LoggedUserGateway loggedUserGateway, RoleGateway roleGateway) {
        super(loggedUserGateway);
        Objects.requireNonNull(roleGateway, "RoleGateway cannot be null.");
        this.roleGateway = roleGateway;
    }

    @Override
    protected Set<Role> doExecute() {
        return roleGateway.findAll();
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RoleRoles.VIEW_ROLE;
    }
}
