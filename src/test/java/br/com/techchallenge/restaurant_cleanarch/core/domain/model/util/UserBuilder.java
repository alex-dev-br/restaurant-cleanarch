package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserBuilder {
    private final Set<Role> roles;
    private UUID id;
    private String name;
    private String email;
    private Address address;
    private UserType userType;

    public UserBuilder() {
        this.id = UUID.randomUUID();
        this.name = "João Silva";
        this.email = "joao@example.com";
        this.address = new AddressBuilder().build();
        this.roles = new HashSet<>();
        this.roles.add(new Role(null, "ADMIN"));
        this.userType = new UserType(1L, "Dono de Restaurante", this.roles);
    }

    public UserBuilder withRole(ForGettingRoleName forGettingRoleName) {
        this.roles.add(new Role(null, forGettingRoleName.getRoleName()));
        return this;
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
        if (userType != null && userType.getRoles() != null) {
            this.roles.addAll(userType.getRoles());
        }
        return this;
    }

    public User build() {
        return new User(id, name, email, address, userType == null ? null : new UserType(this.userType.getId(), this.userType.getName(), this.roles));
    }
}
