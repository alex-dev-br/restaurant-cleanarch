package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class UserType {
    private Long id;
    private String name;   // "Dono de Restaurante", "Cliente"

    public UserType(Long id, String name) {
        Objects.requireNonNull(name, "Name cannot be null.");
        if (name.trim().isBlank()) {
            throw new BusinessException("Name cannot be blank.");
        }
        this.name = name;
        this.id = id;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserType userType)) return false;

        return Objects.equals(id, userType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
