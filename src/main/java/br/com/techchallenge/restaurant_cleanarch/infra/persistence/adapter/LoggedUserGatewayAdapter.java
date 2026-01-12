package br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class LoggedUserGatewayAdapter implements LoggedUserGateway {

    private final UserGateway userGateway;
    private final UUID fakeUserUuid;

    public LoggedUserGatewayAdapter(
            UserGateway userGateway,
            @Value("${app.fake-user-uuid}") String fakeUserUuid
    ) {
        this.userGateway = userGateway;
        this.fakeUserUuid = UUID.fromString(fakeUserUuid);
    }


    // Checa se o userType.roles contém a role solicitada
    @Override
    public boolean hasRole(ForGettingRoleName roleName) {
        return getCurrentUser()
                .map(User::getUserType)
                .map(userType -> userType.getRoles().stream()
                        .map(Role::name)
                        .anyMatch(role -> role.equals(roleName.getRoleName())))
                .orElse(false);
    }

    @Override
    public Optional<User> getCurrentUser() {
        return userGateway.findById(fakeUserUuid);
    }
}
