package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserSummaryOutput;

public class UserPresenter {

    private UserPresenter() {}

    public static UserOutput toOutput(User user) {
        if (user == null) return null;

        UserType userType = user.getUserType();

        return new UserOutput(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress() == null ? null : AddressPresenter.toOutput(user.getAddress()),
                UserTypePresenter.toOutput(userType)
        );
    }

    public static UserSummaryOutput toSummaryOutput(User user) {
        if (user == null) return null;
        return new UserSummaryOutput(user.getId(), user.getName());
    }
}
