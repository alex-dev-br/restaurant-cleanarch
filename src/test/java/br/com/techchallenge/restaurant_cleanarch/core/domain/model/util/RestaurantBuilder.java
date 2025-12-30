package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public class RestaurantBuilder {
    private Long id;
    private String name;
    private Address address;
    private String cuisineType;
    private Set<OpeningHours> openingHours;
    private Set<MenuItem> menu;
    private User owner;

    public RestaurantBuilder() {
        this.name = "Restaurante Exemplo";
        this.address = new AddressBuilder().build();
        this.cuisineType = "Italiana";
        this.openingHours = Set.of(
            new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(20, 30)),
            new OpeningHours(2L, DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
            new OpeningHours(3L, DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
            new OpeningHours(4L, DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(20, 0)),
            new OpeningHours(5L, DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(22, 0)),
            new OpeningHours(6L, DayOfWeek.SATURDAY, LocalTime.of(11, 0), LocalTime.of(22, 0))
        );
        this.owner = new UserBuilder().build();
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
        this.openingHours = openingHours;
        return this;
    }

    public RestaurantBuilder withMenu(Set<MenuItem> menu) {
        this.menu = menu;
        return this;
    }

    public RestaurantBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public Restaurant build() {
        return new Restaurant(id, name, address, cuisineType, openingHours, menu, owner);
    }
}
