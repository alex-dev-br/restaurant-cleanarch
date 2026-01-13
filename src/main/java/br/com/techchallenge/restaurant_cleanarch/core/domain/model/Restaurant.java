package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.HashSet;
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
    private final Set<MenuItem> menu;
    private final User owner;
    private final Set<User> employees;

    public Restaurant(Long id, String name, Address address, String cuisineType, User owner) {
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
            throw new UserCannotBeRestaurantOwnerException();
        }

        this.id = id;
        this.name = name.trim();
        this.address = address;
        this.cuisineType = cuisineType.trim();
        this.openingHours = new HashSet<>();
        this.menu = new HashSet<>();
        this.owner = owner;
        this.employees = new HashSet<>();
    }

    public void addOpeningHours(OpeningHours openingHour) {
        this.openingHours.add(openingHour);
    }

    public void addOpeningHours(Collection<? extends OpeningHours> openingHours) {
        this.openingHours.addAll(openingHours);
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menu.add(menuItem);
    }

    public void addMenuItems(Collection<? extends MenuItem> menuItems) {
        this.menu.addAll(menuItems);
    }

    public void addEmployee(User employee) {
        this.employees.add(employee);
    }

    public void addEmployees(Collection<? extends User> employees) {
        this.employees.addAll(employees);
    }

    public Set<OpeningHours> getOpeningHours() {
        return Set.copyOf(openingHours);
    }

    public Set<MenuItem> getMenuItems() {
        return Set.copyOf(menu);
    }

    public Set<User> getEmployees() {
        return Set.copyOf(this.employees);
    }

    public boolean canBeManagedBy(User currentUser) {
        if (currentUser == null) return false;
        return currentUser.equals(owner) || employees.contains(currentUser);
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