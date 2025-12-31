package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserTypeOutput;

import java.util.stream.Collectors;

public class UserTypePresenter {
    public UserTypeOutput toOutput(UserType userType) {
        return new UserTypeOutput (
            userType.getId(),
            userType.getName(),
            userType.getRoles()
                    .stream()
                    .map(Role::name)
                    .collect(Collectors.toUnmodifiableSet())
        );
    }
}
