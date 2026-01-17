package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.Page;
import br.com.techchallenge.restaurant_cleanarch.core.domain.pagination.PagedQuery;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateUserInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.UserPresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.user.*;

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

    public void update(UpdateUserInput input) {
        Objects.requireNonNull(input, "UpdateUserInput cannot be null.");
        updateUserUseCase.execute(input);
    }

    public UserOutput findById(UUID id) {
        Objects.requireNonNull(id, "User ID cannot be null.");
        var user = getUserByIdUseCase.execute(id);
        return UserPresenter.toOutput(user);
    }

    public Page<UserOutput> findAll(int pageNumber, int pageSize) {
        return listUsersUseCase.execute(new PagedQuery<>(null, pageNumber, pageSize))
                .mapItems(UserPresenter::toOutput);
    }

    public void deleteById(UUID id) {
        Objects.requireNonNull(id, "User ID cannot be null.");
        deleteUserUseCase.execute(id);
    }
}
