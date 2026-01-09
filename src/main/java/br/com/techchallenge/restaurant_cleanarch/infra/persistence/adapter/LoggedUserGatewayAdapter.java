package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;

import java.util.Optional;

public class LoggedUserGatewayAdapter implements LoggedUserGateway {
    @Override
    public boolean hasRole(ForGettingRoleName roleName) {
        return false;
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.empty();
    }
}
