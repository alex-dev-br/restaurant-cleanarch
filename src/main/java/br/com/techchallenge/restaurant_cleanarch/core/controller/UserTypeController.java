package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserTypeInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.UserTypePresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.usertype.*;

import java.util.List;
import java.util.Objects;

public class UserTypeController {

    private final CreateUserTypeUseCase createUserTypeUseCase;
    private final UpdateUserTypeUseCase updateUserTypeUseCase;
    private final DeleteUserTypeUseCase deleteUserTypeUseCase;
    private final GetUserTypeByIdUseCase getUserTypeByIdUseCase;
    private final ListUserTypesUseCase listUserTypesUseCase;

    public UserTypeController (
            CreateUserTypeUseCase createUserTypeUseCase,
            UpdateUserTypeUseCase updateUserTypeUseCase,
            DeleteUserTypeUseCase deleteUserTypeUseCase,
            GetUserTypeByIdUseCase getUserTypeByIdUseCase,
            ListUserTypesUseCase listUserTypesUseCase
    ) {
        Objects.requireNonNull(createUserTypeUseCase, "CreateUserTypeUseCase cannot be null.");
        Objects.requireNonNull(updateUserTypeUseCase, "UpdateUserTypeUseCase cannot be null.");
        Objects.requireNonNull(deleteUserTypeUseCase, "DeleteUserTypeUseCase cannot be null.");
        Objects.requireNonNull(getUserTypeByIdUseCase, "GetByIdUserTypeUseCase cannot be null.");
        Objects.requireNonNull(listUserTypesUseCase, "GetAllUserTypesUseCase cannot be null.");
        this.createUserTypeUseCase = createUserTypeUseCase;
        this.updateUserTypeUseCase = updateUserTypeUseCase;
        this.deleteUserTypeUseCase = deleteUserTypeUseCase;
        this.getUserTypeByIdUseCase = getUserTypeByIdUseCase;
        this.listUserTypesUseCase = listUserTypesUseCase;
    }

    public UserTypeOutput createUserType(CreateUserTypeInput input) {
        var userType = createUserTypeUseCase.execute(input);
        return UserTypePresenter.toOutput(userType);
    }

    public void updateUserType(UpdateUserTypeInput input) {
        updateUserTypeUseCase.execute(input);
    }

    public void deleteUserType(Long id) {
        deleteUserTypeUseCase.execute(id);
    }

    public UserTypeOutput getUserTypeById(Long id) {
        var userType = getUserTypeByIdUseCase.execute(id);
        return UserTypePresenter.toOutput(userType);
    }

    public List<UserTypeOutput> getAllUserTypes() {
        var userTypes = listUserTypesUseCase.execute();
        return userTypes.stream().map(UserTypePresenter::toOutput).toList();
    }
}
