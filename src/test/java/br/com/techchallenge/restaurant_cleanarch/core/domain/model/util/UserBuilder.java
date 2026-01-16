package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.ForGettingRoleName;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.UserSummaryOutput;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserBuilder {

    private static final String DEFAULT_ROLE = "USER";

    private UUID id;
    private String name;
    private String email;
    private Address address;

    private Long userTypeId;
    private String userTypeName;

    private final Set<Role> roles = new HashSet<>();
    private String passwordHash;

    public UserBuilder() {
        withDefaults();
    }

    public UserBuilder withDefaults() {
        this.id = UUID.randomUUID();
        this.name = "João Silva";
        this.email = "joao@example.com";
        this.address = new AddressBuilder().build();

        this.userTypeId = 1L;
        this.userTypeName = "Usuário";

        this.roles.clear();
        this.passwordHash = "HASHED_DEFAULT";
        return this;
    }

    public UserBuilder copy() {
        var b = new UserBuilder().withDefaults();
        b.id = this.id;
        b.name = this.name;
        b.email = this.email;
        b.address = this.address;
        b.userTypeId = this.userTypeId;
        b.userTypeName = this.userTypeName;
        b.passwordHash = this.passwordHash;

        b.roles.clear();
        b.roles.addAll(this.roles);
        return b;
    }

    public UserBuilder withRole(ForGettingRoleName roleName) {
        this.roles.add(new Role(null, roleName.getRoleName()));
        return this;
    }

    public UserBuilder withRoles(Set<Role> roles) {
        this.roles.clear();
        if (roles != null) this.roles.addAll(roles);
        return this;
    }

    public UserBuilder withoutRoles() {
        this.roles.clear();
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

    public UserBuilder withUserTypeMeta(Long id, String name) {
        this.userTypeId = id;
        this.userTypeName = name;
        return this;
    }

    public UserBuilder withUserType(UserType userType) {
        if (userType == null) {
            this.userTypeId = 1L;
            this.userTypeName = "Usuário";
            this.roles.clear();
            return this;
        }

        this.userTypeId = userType.getId();
        this.userTypeName = userType.getName();

        this.roles.clear();
        if (userType.getRoles() != null) this.roles.addAll(userType.getRoles());
        return this;
    }

    public UserBuilder withPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public User build() {
        Set<Role> rolesCopy = new HashSet<>(roles);
        if (rolesCopy.isEmpty()) {
            rolesCopy.add(new Role(null, DEFAULT_ROLE));
        }
        UserType type = new UserType(userTypeId, userTypeName, rolesCopy);
        return new User(id, name, email, address, type, passwordHash);
    }

    public UserSummaryOutput buildSummaryOutput() {
        return new UserSummaryOutput(id, name);
    }
}
