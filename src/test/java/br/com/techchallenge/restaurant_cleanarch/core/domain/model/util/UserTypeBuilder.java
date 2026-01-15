package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Role;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.UserType;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserTypeBuilder {

    private Long id;
    private String name;

    /** Roles em dois formatos porque o input usa String e o domain usa Role */
    private Set<String> roleNames;
    private Set<Role> roles;

    public UserTypeBuilder() {
        withDefaults();
    }

    public UserTypeBuilder withDefaults() {
        this.id = null;
        this.name = "Administrator";

        this.roleNames = new HashSet<>(Set.of("ADMIN"));
        this.roles = new HashSet<>(Set.of(new Role(1L, "ADMIN")));

        return this;
    }

    public UserTypeBuilder copy() {
        UserTypeBuilder b = new UserTypeBuilder().withDefaults();
        b.id = this.id;
        b.name = this.name;

        b.roleNames = new HashSet<>(this.roleNames);
        b.roles = new HashSet<>(this.roles);

        return b;
    }

    public UserTypeBuilder withoutId() {
        this.id = null;
        return this;
    }

    public UserTypeBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserTypeBuilder withRoles(Set<Role> roles) {
        this.roles = new HashSet<>(Objects.requireNonNull(roles, "roles cannot be null"));
        this.roleNames = this.roles.stream().map(Role::name).collect(Collectors.toSet());
        return this;
    }

    public UserTypeBuilder withRoleNames(Set<String> roleNames) {
        this.roleNames = new HashSet<>(Objects.requireNonNull(roleNames, "roleNames cannot be null"));
        this.roles = this.roleNames.stream().map(n -> new Role(null, n)).collect(Collectors.toSet());
        return this;
    }

    public UserTypeBuilder addRoleName(String roleName) {
        if (this.roleNames == null) this.roleNames = new HashSet<>();
        if (this.roles == null) this.roles = new HashSet<>();

        this.roleNames.add(roleName);
        this.roles.add(new Role(null, roleName));
        return this;
    }

    public UserType build() {
        return new UserType(id, name, Set.copyOf(roles));
    }

    public CreateUserTypeInput buildCreateInput() {
        return new CreateUserTypeInput(name, Set.copyOf(roleNames));
    }

    public UpdateUserTypeInput buildUpdateInput(Long id) {
        return new UpdateUserTypeInput(id, name, Set.copyOf(roleNames));
    }

    public UpdateUserTypeInput buildUpdateInput() {
        if (id == null) throw new IllegalStateException("id must not be null to build UpdateUserTypeInput");
        return new UpdateUserTypeInput(id, name, Set.copyOf(roleNames));
    }
}
