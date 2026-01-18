package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;

import java.util.*;

public interface UserGateway {
    Optional<User> findById(UUID uuid);

    boolean existsUserWithEmail(String email);

    User save(User user);

    Page<User> findAll(PagedQuery<Void> input);


    void deleteById(UUID id);
}
