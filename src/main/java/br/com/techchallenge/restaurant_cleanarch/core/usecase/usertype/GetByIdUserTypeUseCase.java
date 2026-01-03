package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;

import java.util.Objects;

public class GetByIdUserTypeUseCase {


    private final UserTypeGateway userTypeGateway;
    private final LoggedUserGateway loggedUserGateway;

    public GetByIdUserTypeUseCase(UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggerUserGateway cannot be null");
        this.userTypeGateway = userTypeGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public UserType execute(Long id) {
        Objects.requireNonNull(id, "Id of user type cannot be null.");

        if (!loggedUserGateway.hasRole(UserTypeRoles.VIEW_USER_TYPE)) {
            throw new OperationNotAllowedException("The current user does not have permission to get user types.");
        }

        return userTypeGateway
                .findById(id)
                .orElseThrow(() -> new BusinessException("User type not found."));
    }
}
