package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;

public interface LoggedUserGateway {
    boolean hasRole(ForGettingRoleName roleName);
}
