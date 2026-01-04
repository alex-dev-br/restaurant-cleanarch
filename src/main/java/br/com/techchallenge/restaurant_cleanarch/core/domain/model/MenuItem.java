package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@ToString
public class MenuItem {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final Boolean restaurantOnly;
    private final String photoPath;

    private final Restaurant restaurant;

    public MenuItem(Long id, String name, String description, BigDecimal price, Boolean restaurantOnly, String photoPath, Restaurant restaurant) {
        Objects.requireNonNull(name, "O nome do item não pode ser nulo.");
        Objects.requireNonNull(price, "O preço não pode ser nulo.");
        Objects.requireNonNull(restaurantOnly, "A disponibilidade para restaurante apenas não pode ser nula.");
        Objects.requireNonNull(photoPath, "O caminho da foto não pode ser nulo.");
        Objects.requireNonNull(restaurant, "O restaurante não pode ser nulo.");

        if (name.trim().isBlank()) {
            throw new BusinessException("O nome do item não pode ser vazio.");
        }
        if (photoPath.trim().isBlank()) {
            throw new BusinessException("O caminho da foto não pode ser vazio.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O preço deve ser maior que zero.");
        }

        this.id = id;
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.price = price;
        this.restaurantOnly = restaurantOnly;
        this.photoPath = photoPath.trim();
        this.restaurant = restaurant;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem that)) return false;

        if (this.id != null || that.id != null) {
            return Objects.equals(this.id, that.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : super.hashCode();
    }
}
