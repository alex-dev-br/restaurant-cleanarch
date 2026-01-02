package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserTypeWithoutRolesException;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.Set;

@Getter
@ToString
public class UserType {

    private final Long id;
    private final String name;
    private final Set<Role> roles;

    public UserType(Long id, String name, Set<Role> roles) throws NullPointerException, BusinessException {
        Objects.requireNonNull(name, "Name cannot be null.");
        Objects.requireNonNull(roles, "Roles cannot be null.");

        if (name.isBlank()) throw new BusinessException("Name cannot be blank.");
        if (roles.isEmpty()) throw new UserTypeWithoutRolesException();

        this.id = id;
        this.name = name;
        this.roles = Set.copyOf(roles);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserType userType)) return false;

        if (this.id != null && userType.id != null) {
            return Objects.equals(this.id, userType.id);
        }

        return Objects.equals(this.name, userType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
