package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;

import java.util.Optional;

public interface UserTypeGateway {
    UserType save(UserType userType);
    boolean existsUserTypeWithName(String name);
    Optional<UserType> findByName(String name);
    Optional<UserType> findById(Long id);
}
