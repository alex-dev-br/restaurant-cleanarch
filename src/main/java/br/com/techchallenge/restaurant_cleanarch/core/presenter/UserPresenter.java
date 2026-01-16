package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RoleOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserSummaryOutput;

import java.util.Set;
import java.util.stream.Collectors;

public class UserPresenter {

    private UserPresenter() {}

    public static UserOutput toOutput(User user) {
        if (user == null) return null;

        UserType userType = user.getUserType();

        Set<RoleOutput> roles = (userType == null || userType.getRoles() == null)
                ? Set.of()
                : userType.getRoles().stream()
                .map(RolePresenter::toOutput)
                .collect(Collectors.toUnmodifiableSet());

        return new UserOutput(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress() == null ? null : AddressPresenter.toOutput(user.getAddress()),
                userType == null ? null : UserTypePresenter.toOutput(userType),
                roles
        );
    }

    public static UserSummaryOutput toSummary(User user) {
        if (user == null) return null;
        return new UserSummaryOutput(user.getId(), user.getName());
    }

    public static UserSummaryOutput toSummaryOutput(User user) {
        return toSummary(user);
    }
}
