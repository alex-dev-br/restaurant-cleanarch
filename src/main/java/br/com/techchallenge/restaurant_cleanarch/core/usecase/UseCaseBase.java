package br.com.techchallenge.restaurant_cleanarch.core.usecase;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;

import java.util.Objects;

public abstract class UseCaseBase<T, R> {

    private final LoggedUserGateway loggedUserGateway;

    protected UseCaseBase(LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        this.loggedUserGateway = loggedUserGateway;
    }

    public  R execute(T input) {
        Objects.requireNonNull(input,  "Input cannot be null.");

        boolean isAllowed = isPublicAccessAllowed() || loggedUserGateway.hasRole(getRequiredRole());
        if (!isAllowed)
            throw new OperationNotAllowedException("The current user does not have permission to update restaurants.");

        return doExecute(input);
    }

    protected abstract R doExecute(T t);

    protected abstract ForGettingRoleName getRequiredRole();

    protected boolean isPublicAccessAllowed() {
        return false;
    }
}
