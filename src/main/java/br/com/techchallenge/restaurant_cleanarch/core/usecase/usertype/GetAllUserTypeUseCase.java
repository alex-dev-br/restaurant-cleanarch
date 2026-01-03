package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;

import java.util.Objects;
import java.util.Set;

public class GetAllUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;
    private final LoggedUserGateway loggedUserGateway;

    public GetAllUserTypeUseCase(UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggerUserGateway cannot be null");
        this.userTypeGateway = userTypeGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public Set<UserType> execute() {
        if (!loggedUserGateway.hasRole(UserTypeRoles.VIEW_USER_TYPE)) {
            throw new OperationNotAllowedException("The current user does not have permission to get all user types.");
        }
        return userTypeGateway.findAll();
    }
}
