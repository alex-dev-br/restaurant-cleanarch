package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@ToString
public class MenuItem {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean restaurantOnly;
    private String photoPath;

    public MenuItem(Long id, String name, String description, BigDecimal price, Boolean restaurantOnly, String photoPath) {
        Objects.requireNonNull(name, "Name cannot be null.");
        Objects.requireNonNull(price, "Price cannot be null.");
        Objects.requireNonNull(restaurantOnly, "RestaurantOnly cannot be null.");
        Objects.requireNonNull(photoPath, "PhotoPath cannot be null.");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero.");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurantOnly = restaurantOnly;
        this.photoPath = photoPath;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MenuItem menuItem)) return false;

        return Objects.equals(id, menuItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
