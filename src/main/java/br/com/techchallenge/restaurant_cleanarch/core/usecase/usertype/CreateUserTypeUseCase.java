package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.InvalidRoleException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeWithoutRolesException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserTypeInput;

import java.util.Objects;
import java.util.stream.Collectors;

public class CreateUserTypeUseCase {

    private final RoleGateway roleGateway;
    private final UserTypeGateway userTypeGateway;
    private final LoggedUserGateway loggedUserGateway;

    public CreateUserTypeUseCase(RoleGateway roleGateway, UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(roleGateway, "RoleGateway cannot be null");
        Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggerUserGateway cannot be null");

        this.roleGateway = roleGateway;
        this.userTypeGateway = userTypeGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public UserType execute(CreateUserTypeInput input) {
        Objects.requireNonNull(input, "UserTypeInput cannot be null.");

        if (!loggedUserGateway.hasRole(UserTypeRoles.CREATE_USER_TYPE))
            throw new OperationNotAllowedException("The current user does not have permission to create user types.");

        var roles = roleGateway.getRolesByName(input.roles());
        if (roles.isEmpty()) throw new UserTypeWithoutRolesException();
        if (roles.size() != input.roles().size()) {
            var existingRolesNames = roles.stream().map(Role::name).collect(Collectors.toUnmodifiableSet());
            var invalidRoles = input.roles().stream().filter(role -> !existingRolesNames.contains(role)).collect(Collectors.toUnmodifiableSet());
            throw new InvalidRoleException(invalidRoles);
        }

        var exists = userTypeGateway.existsUserTypeWithName(input.name());
        if (exists) throw new UserTypeNameIsAlreadyInUseException();

        UserType userType = new UserType(null, input.name(), roles);

        return userTypeGateway.save(userType);
    }
}
