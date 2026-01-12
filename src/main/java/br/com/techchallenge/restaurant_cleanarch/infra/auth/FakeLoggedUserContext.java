package br.com.techchallenge.restaurant_cleanarch.infra.auth;


import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;

public class FakeLoggedUserContext {
    private final User user;

    public FakeLoggedUserContext(User user) {
        this.user = user;
    }

    public User get() { return user; }
}
