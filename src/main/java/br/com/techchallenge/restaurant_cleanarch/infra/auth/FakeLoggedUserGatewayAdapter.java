package br.com.techchallenge.restaurant_cleanarch.infra.auth;


import br.com.techchallenge.restaurant_cleanarch.core.domain.model.*;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("dev")
public class FakeLoggedUserGatewayAdapter implements LoggedUserGateway {

    private final FakeLoggedUserContext context;

    private static final Logger log = LoggerFactory.getLogger(FakeLoggedUserGatewayAdapter.class);

    public FakeLoggedUserGatewayAdapter(FakeLoggedUserContext context) {
        this.context = context;
        log.info(">>> FAKE LOGGED USER GATEWAY ADAPTER IN USE (profile dev) <<<");
    }

    @Override
    public boolean hasRole(ForGettingRoleName roleName) {
        if (roleName == null) return false;

        return getCurrentUser()
                .map(User::getUserType)
                .map(UserType::getRoles)
                .stream()
                .flatMap(Set::stream)
                .map(Role::name)
                .anyMatch(roleName.getRoleName()::equals);
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(context.get());
    }

}
