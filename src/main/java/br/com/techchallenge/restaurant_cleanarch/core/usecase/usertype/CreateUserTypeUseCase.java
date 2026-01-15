package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.InvalidRoleException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeWithoutRolesException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RoleGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;
import java.util.stream.Collectors;

public class CreateUserTypeUseCase extends UseCaseBase<CreateUserTypeInput, UserType> {

    private final RoleGateway roleGateway;
    private final UserTypeGateway userTypeGateway;

    public CreateUserTypeUseCase(RoleGateway roleGateway, UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.roleGateway = Objects.requireNonNull(roleGateway, "RoleGateway cannot be null.");
        this.userTypeGateway = Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null.");
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserTypeRoles.CREATE_USER_TYPE;
    }

    @Override
    protected UserType doExecute(CreateUserTypeInput input) {
        var roles = roleGateway.getRolesByName(input.roles());

        if (roles.isEmpty()) throw new UserTypeWithoutRolesException();

        if (roles.size() != input.roles().size()) {
            var existingRolesNames = roles.stream()
                    .map(Role::name)
                    .collect(Collectors.toUnmodifiableSet());

            var invalidRoles = input.roles().stream()
                    .filter(role -> !existingRolesNames.contains(role))
                    .collect(Collectors.toUnmodifiableSet());

            throw new InvalidRoleException(invalidRoles);
        }

        if (userTypeGateway.existsUserTypeWithName(input.name())) {
            throw new UserTypeNameIsAlreadyInUseException();
        }

        return userTypeGateway.save(new UserType(null, input.name(), roles));
    }
}
