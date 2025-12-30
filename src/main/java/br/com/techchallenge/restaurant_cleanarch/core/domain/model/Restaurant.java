package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@ToString
public class Restaurant {
    private Long id;
    private String name;
    private Address address;
    private String cuisineType;
    private Set<OpeningHours> openingHours;
    private Set<MenuItem> menu;
    private User owner;

    public Restaurant(Long id, String name, Address address, String cuisineType, Set<OpeningHours> openingHours, Set<MenuItem> menu, User owner) {
        if (owner == null || !owner.isRestaurantOwner()) {
            throw new BusinessException("O restaurante deve ter um dono válido.");
        }
        this.id = id;
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.openingHours = openingHours;
        this.menu = menu;
        this.owner = owner;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Restaurant that)) return false;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
