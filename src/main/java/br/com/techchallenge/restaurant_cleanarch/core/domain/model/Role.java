package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;

import java.util.Objects;

public record Role(Long id, String name) {

    public Role {
        Objects.requireNonNull(name, "Role name cannot be null.");
        if (name.isBlank()) throw new BusinessException("Role name cannot be blank.");
    }
}
