package br.com.techchallenge.restaurant_cleanarch.core.usecase;

import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;

public abstract class UseCaseWithoutInput<T> extends UseCase {

    protected UseCaseWithoutInput(LoggedUserGateway loggedUserGateway) {
        super(loggedUserGateway);
    }

    public T execute() {
        validateAccess();
        return doExecute();
    }

    protected abstract T doExecute();
}
