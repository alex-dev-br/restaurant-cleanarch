package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;

import java.util.*;

public interface UserGateway {
    Optional<User> findByUuid(UUID uuid);

    boolean existsUserWithEmail(String email);

    User save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    boolean existsByEmail(String email);

    void deleteById(UUID id);
}
