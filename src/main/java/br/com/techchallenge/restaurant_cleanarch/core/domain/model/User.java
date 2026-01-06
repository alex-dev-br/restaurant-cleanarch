package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public class User {
    private UUID id;
    private String name;
    private EmailAddress emailAddress;
    private Address address;
    private UserType userType;

    public User(UUID id, String name, EmailAddress emailAddress, Address address, UserType userType) {
        Objects.requireNonNull(name, "Name cannot be null.");
        Objects.requireNonNull(emailAddress, "Email cannot be null.");
        Objects.requireNonNull(userType, "User type cannot be null.");

        if(name.isBlank()) {
            throw new BusinessException("Name cannot be blank.");
        }

        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.address = address;
        this.userType = userType;
    }

    public boolean isRestaurantOwner() {
        return userType != null  && "Dono de Restaurante".equalsIgnoreCase(this.userType.getName());
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
