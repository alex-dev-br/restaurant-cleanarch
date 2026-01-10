package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
    Optional<User> findByUuid(UUID uuid);

    boolean existsUserWithEmail(String email);

    User save(User user);
}
