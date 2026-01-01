package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.UserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.UserTypePresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.CreateUserTypeUseCase;

import java.util.Objects;

public class UserTypeController {

    private final CreateUserTypeUseCase createUserTypeUseCase;

    public UserTypeController(CreateUserTypeUseCase createUserTypeUseCase) {
        Objects.requireNonNull(createUserTypeUseCase, "CreateUserTypeUseCase cannot be null.");
        this.createUserTypeUseCase = createUserTypeUseCase;
    }

    public UserTypeOutput createUserType(UserTypeInput input) {
        var userType = createUserTypeUseCase.execute(input);
        return UserTypePresenter.toOutput(userType);
    }

}
