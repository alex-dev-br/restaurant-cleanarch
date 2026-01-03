package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;

import java.util.Objects;

public class DeleteUserTypeUseCase {

    private final UserTypeGateway userTypeGateway;
    private final LoggedUserGateway loggedUserGateway;

    public DeleteUserTypeUseCase(UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null");
        Objects.requireNonNull(loggedUserGateway, "LoggerUserGateway cannot be null");
        this.userTypeGateway = userTypeGateway;
        this.loggedUserGateway = loggedUserGateway;
    }

    public void execute(Long id) {
        Objects.requireNonNull(id, "Id of user type cannot be null.");

        if (!loggedUserGateway.hasRole(UserTypeRoles.DELETE_USER_TYPE)) {
            throw new OperationNotAllowedException("The current user does not have permission to delete user types.");
        }

        userTypeGateway.findById(id).orElseThrow(() -> new BusinessException("User type not found."));

        if (userTypeGateway.isInUse(id)) {
            throw new UserTypeInUseException();
        }

        userTypeGateway.delete(id);
    }
}
