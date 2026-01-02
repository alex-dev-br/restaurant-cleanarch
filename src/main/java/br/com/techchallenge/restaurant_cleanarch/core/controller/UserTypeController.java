package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.UserTypePresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.CreateUserTypeUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.DeleteUserTypeUseCase;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.UpdateUserTypeUseCase;

import java.util.Objects;

public class UserTypeController {

    private final CreateUserTypeUseCase createUserTypeUseCase;
    private final UpdateUserTypeUseCase updateUserTypeUseCase;
    private final DeleteUserTypeUseCase deleteUserTypeUseCase;

    public UserTypeController (
            CreateUserTypeUseCase createUserTypeUseCase,
            UpdateUserTypeUseCase updateUserTypeUseCase,
            DeleteUserTypeUseCase deleteUserTypeUseCase
    ) {
        Objects.requireNonNull(createUserTypeUseCase, "CreateUserTypeUseCase cannot be null.");
        Objects.requireNonNull(updateUserTypeUseCase, "UpdateUserTypeUseCase cannot be null.");
        Objects.requireNonNull(deleteUserTypeUseCase, "DeleteUserTypeUseCase cannot be null.");
        this.createUserTypeUseCase = createUserTypeUseCase;
        this.updateUserTypeUseCase = updateUserTypeUseCase;
        this.deleteUserTypeUseCase = deleteUserTypeUseCase;
    }

    public UserTypeOutput createUserType(UserTypeInput input) {
        var userType = createUserTypeUseCase.execute(input);
        return UserTypePresenter.toOutput(userType);
    }

    public void updateUserType(UpdateUserTypeInput input) {
        updateUserTypeUseCase.execute(input);
    }

    public void deleteUserType(Long id) {
        deleteUserTypeUseCase.execute(id);
    }
}
