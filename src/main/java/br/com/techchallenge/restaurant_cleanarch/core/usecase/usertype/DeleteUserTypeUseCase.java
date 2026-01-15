package br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UseCaseBase;

import java.util.Objects;

public class DeleteUserTypeUseCase extends UseCaseBase<Long, Void> {

    private final UserTypeGateway userTypeGateway;

    public DeleteUserTypeUseCase(UserTypeGateway userTypeGateway, LoggedUserGateway loggedUserGateway) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.userTypeGateway = Objects.requireNonNull(userTypeGateway, "UserTypeGateway cannot be null.");
    }

    @Override
    public Void execute(Long id) {
        return super.execute(id);
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return UserTypeRoles.DELETE_USER_TYPE;
    }

    @Override
    protected Void doExecute(Long id) {
        userTypeGateway.findById(id).orElseThrow(() -> new BusinessException("User type not found."));

        if (userTypeGateway.isInUse(id)) {
            throw new UserTypeInUseException();
        }

        userTypeGateway.delete(id);
        return null;
    }
}
