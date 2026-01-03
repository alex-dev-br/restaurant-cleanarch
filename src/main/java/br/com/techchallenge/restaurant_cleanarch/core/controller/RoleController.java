package br.com.techchallenge.restaurant_cleanarch.core.controller;

import br.com.techchallenge.restaurant_cleanarch.core.outbound.RoleOutput;
import br.com.techchallenge.restaurant_cleanarch.core.presenter.RolePresenter;
import br.com.techchallenge.restaurant_cleanarch.core.usecase.role.GetAllRolesUseCase;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleController {

    private final GetAllRolesUseCase getAllRolesUseCase;

    public RoleController(GetAllRolesUseCase getAllRolesUseCase) {
        Objects.requireNonNull(getAllRolesUseCase, "GetAllRolesUseCase cannot be null.");
        this.getAllRolesUseCase = getAllRolesUseCase;
    }

    public Set<RoleOutput> getAllRoles() {
        var roles = getAllRolesUseCase.execute();
        return roles.stream().map(RolePresenter::toOutput).collect(Collectors.toSet());
    }
}
