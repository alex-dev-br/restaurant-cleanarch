package br.com.techchallenge.restaurant_cleanarch.core.usecase.impl;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserTypeGateway;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.CreateUserTypeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserTypeUseCaseImpl implements CreateUserTypeUseCase {

    private final UserTypeGateway gateway;

    @Override
    public UserType execute(UserType userType) {
        userType.validate();
        return gateway.save(userType);
    }
}