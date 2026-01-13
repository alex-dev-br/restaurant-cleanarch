package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.UserPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ListUsersUseCase listUsersUseCase;

    public UserController(
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            GetUserByIdUseCase getUserByIdUseCase,
            ListUsersUseCase listUsersUseCase
    ) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase, "CreateUserUseCase cannot be null.");
        this.updateUserUseCase = Objects.requireNonNull(updateUserUseCase, "UpdateUserUseCase cannot be null.");
        this.deleteUserUseCase = Objects.requireNonNull(deleteUserUseCase, "DeleteUserUseCase cannot be null.");
        this.getUserByIdUseCase = Objects.requireNonNull(getUserByIdUseCase, "GetUserByIdUseCase cannot be null.");
        this.listUsersUseCase = Objects.requireNonNull(listUsersUseCase, "ListUsersUseCase cannot be null.");
    }

    public UserOutput create(CreateUserInput input) {
        Objects.requireNonNull(input, "CreateUserInput cannot be null.");
        var user = createUserUseCase.execute(input);
        return UserPresenter.toOutput(user);
    }

    public UserOutput update(UUID id, UpdateUserInput input) {
        Objects.requireNonNull(id, "User ID cannot be null.");
        Objects.requireNonNull(input, "UpdateUserInput cannot be null.");
        var user = updateUserUseCase.execute(id, input);
        return UserPresenter.toOutput(user);
    }

    public UserOutput findById(UUID id) {
        Objects.requireNonNull(id, "User ID cannot be null.");
        var user = getUserByIdUseCase.execute(id);
        return UserPresenter.toOutput(user);
    }

    public List<UserOutput> findAll() {
        return listUsersUseCase.execute()
                .stream()
                .map(UserPresenter::toOutput)
                .toList();
    }

    public void deleteById(UUID id) {
        Objects.requireNonNull(id, "User ID cannot be null.");
        deleteUserUseCase.execute(id);
    }
}
