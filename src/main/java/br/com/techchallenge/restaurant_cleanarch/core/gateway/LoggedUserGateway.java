package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;

import java.util.Optional;

public interface LoggedUserGateway {
    boolean hasRole(ForGettingRoleName roleName);

    Optional<User> getCurrentUser();
}
