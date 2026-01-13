package br.com.techchallenge.restaurant_cleanarch.infra.auth;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.*;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("!dev")
public class LoggedUserGatewayAdapter implements LoggedUserGateway {

    private final UserGateway userGateway;

    public LoggedUserGatewayAdapter(UserGateway userGateway) {
        this.userGateway = userGateway;
    }


    @Override
    public boolean hasRole(ForGettingRoleName roleName) {
        if (roleName == null) return false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        String expected = roleName.getRoleName();

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(expected::equals);
    }

    @Override
    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();

        return extractUserId(auth)
                .flatMap(userGateway::findById);
    }

    private Optional<UUID> extractUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal == null) return Optional.empty();

        if (principal instanceof UUID uuid) return Optional.of(uuid);

        if (principal instanceof String raw) {
            try {
                return Optional.of(UUID.fromString(raw));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }

        // Ajustar aqui para usar UserDetails/JWT se necessário
        // Hoje cobre apenas UUID ou String (uuid) como principal
        return Optional.empty();
    }

}
