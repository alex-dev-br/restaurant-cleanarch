package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;

import java.util.Set;
import java.util.UUID;

public class UserBuilder {
    private UUID id;
    private String name;
    private String email;
    private Address address;
    private UserType userType;

    public UserBuilder() {
        this.id = null; // o ID de uma entidade só deve ser gerado na camada de infraestrutura (entidade nova não tem id até ser persistida)
        this.name = "João Silva";
        this.email = "joao@example.com";
        this.address = new AddressBuilder().build();
//        Set<Role> roles = Set.of(new Role(null, "ADMIN"));
        Set<Role> ownerRoles = Set.of(new Role(null, "RESTAURANT_OWNER"));
        this.userType = new UserType(null, "Dono de Restaurante", ownerRoles);
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
