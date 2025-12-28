package br.com.techchallenge.restaurant_cleanarch.core.usecase;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;

public interface CreateUserTypeUseCase {

    UserType execute(UserType userType);
}
