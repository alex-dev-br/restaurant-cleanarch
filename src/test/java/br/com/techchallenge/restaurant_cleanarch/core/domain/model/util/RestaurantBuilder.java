package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class RestaurantBuilder {

    private Long id;
    private String name;
    private Address address;
    private String cuisineType;
    private Set<OpeningHours> openingHours;
    private Set<MenuItem> menu;
    private Set<User> employees;
    private User owner;

    public RestaurantBuilder() {
        withDefaults();
    }

    public RestaurantBuilder withDefaults() {
        this.id = null;
        this.name = "Restaurante Exemplo";
        this.address = new AddressBuilder().build();
        this.cuisineType = "Italiana";

        this.openingHours = new HashSet<>(Set.of(
                new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(20, 30)),
                new OpeningHours(2L, DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(3L, DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(4L, DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
                new OpeningHours(5L, DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(22, 0)),
                new OpeningHours(6L, DayOfWeek.SATURDAY, LocalTime.of(11, 0), LocalTime.of(22, 0))
        ));

        this.menu = new HashSet<>();
        this.employees = new HashSet<>();

        this.owner = new UserBuilder()
                .withDefaults()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .withName("Dono do Restaurante")
                .withEmail("dono@exemplo.com")
                .build();

        return this;
    }

    public RestaurantBuilder copy() {
        RestaurantBuilder b = new RestaurantBuilder().withDefaults();
        b.id = this.id;
        b.name = this.name;
        b.address = this.address;
        b.cuisineType = this.cuisineType;

        b.openingHours = new HashSet<>(this.openingHours);
        b.menu = new HashSet<>(this.menu);
        b.employees = new HashSet<>(this.employees);

        b.owner = this.owner;
        return b;
    }

    public RestaurantBuilder withoutId() {
        this.id = null;
        return this;
    }

    public RestaurantBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public RestaurantBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public RestaurantBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public RestaurantBuilder withCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
        return this;
    }

    public RestaurantBuilder withOpeningHours(Set<OpeningHours> openingHours) {
        this.openingHours = (openingHours == null) ? new HashSet<>() : new HashSet<>(openingHours);
        return this;
    }

    public RestaurantBuilder withMenu(Set<MenuItem> menu) {
        this.menu = (menu == null) ? new HashSet<>() : new HashSet<>(menu);
        return this;
    }

    public RestaurantBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    /** Atalho super útil para testes de autorização por UUID */
    public RestaurantBuilder withOwnerId(UUID ownerId) {
        this.owner = new UserBuilder()
                .withDefaults()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();
        return this;
    }

    public RestaurantBuilder withEmployee(User employee) {
        this.employees.add(employee);
        return this;
    }

    public RestaurantBuilder withEmployees(Collection<? extends User> employees) {
        if (employees != null) this.employees.addAll(employees);
        return this;
    }

    public Restaurant build() {
        var restaurant = new Restaurant(id, name, address, cuisineType, owner);

        // evita NPE se alguém setar null via builder
        restaurant.addOpeningHours(openingHours == null ? Set.of() : openingHours);
        restaurant.addMenuItems(menu == null ? Set.of() : menu);
        restaurant.addEmployees(employees == null ? Set.of() : employees);

        return restaurant;
    }
}
