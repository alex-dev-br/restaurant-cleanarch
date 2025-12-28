package br.com.techchallenge.restaurant_cleanarch.core.gateway;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;

public interface UserTypeGateway {

    UserType save(UserType userType);

    // Adicionar outros métodos (findById, findAll, deleteById etc.)

}
