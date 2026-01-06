package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.Set;

@Getter
@ToString
public class Restaurant {
    private final Long id;
    private final String name;
    private final Address address;
    private final String cuisineType;
    private final Set<OpeningHours> openingHours;
    private final Menu menu;
    private final User owner;

    public Restaurant(Long id, String name, Address address, String cuisineType, Set<OpeningHours> openingHours, Menu menu, User owner) {
        Objects.requireNonNull(name, "O nome do restaurante não pode ser nulo.");
        Objects.requireNonNull(address, "O endereço não pode ser nulo.");
        Objects.requireNonNull(cuisineType, "O tipo de cozinha não pode ser nulo.");
        Objects.requireNonNull(owner, "O dono do restaurante não pode ser nulo.");

        if (name.isBlank()) {
            throw new BusinessException("O nome do restaurante não pode ser vazio.");
        }
        if (cuisineType.isBlank()) {
            throw new BusinessException("O tipo de cozinha não pode ser vazio.");
        }
        if (!owner.canOwnRestaurant()) {
            throw new BusinessException("O restaurante deve ter um dono com permissão de proprietário.");
        }

        this.id = id;
        this.name = name.trim();
        this.address = address;
        this.cuisineType = cuisineType.trim();
        this.openingHours = openingHours == null ? Set.of() : Set.copyOf(openingHours);
        this.menu = menu;
        this.owner = owner;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant that)) return false;

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