package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public class User {
    private UUID id;
    private String name;
    private String email;
    private Address address;
    private UserType userType;

    public User(UUID id, String name, String email, Address address, UserType userType) {
        Objects.requireNonNull(name, "Name cannot be null.");
        Objects.requireNonNull(email, "Email cannot be null.");
        Objects.requireNonNull(userType, "User type cannot be null.");

        if(name.trim().isBlank()) {
            throw new BusinessException("Name cannot be blank.");
        }
        if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException("Email inválido.");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.userType = userType;
    }

    public boolean isRestaurantOwner() {
        return "Dono de Restaurante".equalsIgnoreCase(this.userType.getName());
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof User user)) return false;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
