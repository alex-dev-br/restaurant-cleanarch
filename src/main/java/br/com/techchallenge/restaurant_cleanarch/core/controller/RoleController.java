package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.outbound.RoleOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.RolePresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.role.ListRolesUseCase;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleController {

    private final ListRolesUseCase listRolesUseCase;

    public RoleController(ListRolesUseCase listRolesUseCase) {
        Objects.requireNonNull(listRolesUseCase, "GetAllRolesUseCase cannot be null.");
        this.listRolesUseCase = listRolesUseCase;
    }

    public Set<RoleOutput> getAllRoles() {
        var roles = listRolesUseCase.execute();
        return roles.stream().map(RolePresenter::toOutput).collect(Collectors.toSet());
    }
}
