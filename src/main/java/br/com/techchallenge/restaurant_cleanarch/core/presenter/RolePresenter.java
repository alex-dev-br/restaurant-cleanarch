package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RoleOutput;

public class RolePresenter {
    private RolePresenter() {}

    public static RoleOutput toOutput(Role role) {
        return new RoleOutput(role.id(), role.name());
    }
}
