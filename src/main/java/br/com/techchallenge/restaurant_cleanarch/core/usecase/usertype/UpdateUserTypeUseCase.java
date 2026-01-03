package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.*;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;

import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateUserTypeUseCase {

    private final RoleGateway roleGateway;
    private final UserTypeGateway userTypeGateway;
    private final LoggedUserGateway loggedUserGateway;

    public UpdateUserTypeUseCase(RoleGateway roleGateway, UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(roleGateway, "RoleGateway cannot be null");
        Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggerUserGateway cannot be null");

        this.roleGateway = roleGateway;
        this.userTypeGateway = userTypeGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public void execute(UpdateUserTypeInput input) {
        Objects.requireNonNull(input, "UpdateUserTypeInput cannot be null.");

        if (!loggedUserGateway.hasRole(UserTypeRoles.UPDATE_USER_TYPE)) {
            throw new OperationNotAllowedException("The current user does not have permission to update user types.");
        }

        userTypeGateway.findById(input.id()).orElseThrow(() -> new BusinessException("User type not found."));

        var roles = roleGateway.getRolesByName(input.roles());
        if (roles.isEmpty()) throw new UserTypeWithoutRolesException();
        if (roles.size() != input.roles().size()) {
            var existingRolesNames = roles.stream().map(Role::name).collect(Collectors.toUnmodifiableSet());
            var invalidRoles = input.roles().stream().filter(role -> !existingRolesNames.contains(role)).collect(Collectors.toUnmodifiableSet());
            throw new InvalidRoleException(invalidRoles);
        }

        var optionalUserType = userTypeGateway.findByName(input.name());
        optionalUserType.ifPresent(userType -> {
            if (!userType.getId().equals(input.id())) {
                throw new UserTypeNameIsAlreadyInUseException();
            }
        });

        var userType = new UserType(input.id(), input.name(), roles);
        userTypeGateway.save(userType);
    }
}
