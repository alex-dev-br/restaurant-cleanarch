package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;

import java.util.UUID;

public class UserBuilder {
    private UUID id;
    private String name;
    private String email;
    private Address address;
    private UserType userType;

    public UserBuilder() {
        this.id = null;
        this.name = "João Silva";
        this.email = "joao@example.com";
        this.address = new AddressBuilder().build();
        this.userType = new UserType(1L, "Dono de Restaurante");
    }

    public UserBuilder withoutId() {
        this.id = null;
        return this;
    }

    public UserBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public UserBuilder withUserType(UserType userType) {
        this.userType = userType;
        return this;
    }

    public User build() {
        return new User(id, name, email, address, userType);
    }
}
