package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;

import java.util.*;

public interface UserGateway {
    Optional<User> findById(UUID uuid);

    boolean existsUserWithEmail(String email);

    User save(User user);

    List<User> findAll();

    boolean existsByEmail(String email);

    void deleteById(UUID id);
}
