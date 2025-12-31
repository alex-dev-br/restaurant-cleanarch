package br.com.techchallenge.restaurant_cleanarch.core.exception;

import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@Getter
public class InvalidRoleException extends BusinessException {

    private final Set<String> invalidRoles;

    public InvalidRoleException(Set<String> invalidRoles) {
        super("Invalid roles: %s .".formatted(String.join(", ", Objects.requireNonNull(invalidRoles, "Set of invalid roles cannot be null."))));
        this.invalidRoles = Set.copyOf(invalidRoles);
    }
}
