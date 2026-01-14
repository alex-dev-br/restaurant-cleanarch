package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserSummaryOutput;

public class UserPresenter {

    private UserPresenter(){}

    public static UserSummaryOutput toSummaryOutput(User user) {
        return new UserSummaryOutput(user.getId(), user.getName());
    }
}
