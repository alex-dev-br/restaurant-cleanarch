package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.*;
import java.util.regex.Pattern;

@Getter
@ToString(exclude = "passwordHash")
public class User {
    private final UUID id;
    private final String name;
    private final String email;
    private final Address address;
    private final UserType userType;
    private final String passwordHash;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public User(UUID id, String name, String email, Address address, UserType userType, String passwordHash) {
        Objects.requireNonNull(name, "Name cannot be null.");
        Objects.requireNonNull(email, "Email cannot be null.");
        Objects.requireNonNull(userType, "User type cannot be null.");
        Objects.requireNonNull(passwordHash, "Password hash cannot be null.");

        if(name.trim().isBlank()) throw new BusinessException("Name cannot be blank.");

        if(!EMAIL_PATTERN.matcher(email).matches()) throw new BusinessException("Email inválido.");

        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.userType = userType;
        this.passwordHash = passwordHash;
    }

    public boolean canOwnRestaurant() {
        return this.userType.getRoles()
                .stream()
                .map(Role::name)
                .anyMatch(UserRoles.RESTAURANT_OWNER.getRoleName()::equals);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;  // ← reflexividade e otimização
        if (!(o instanceof User user)) return false;

        // Se id não é null, compara pelo id
        if (id != null) {
            return Objects.equals(id, user.id);
        }

        // Se id é null nos dois, considera diferentes (referência)
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : super.hashCode();
    }
}
