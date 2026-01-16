package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserRoles;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.AddressOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.OpeningHoursOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantPublicOutput;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RestaurantBuilder {

    private Long id;
    private String name;
    private Address address;
    private String cuisineType;
    private Set<OpeningHours> openingHours;
    private Set<MenuItem> menuItems;
    private Set<User> employees;
    private User owner;

    public RestaurantBuilder() {
        withDefaults();
    }

    public RestaurantBuilder withDefaults() {
        this.id = 1L;
        this.name = "Restaurant Name";
        this.address = new AddressBuilder().build();
        this.cuisineType = "Cuisine Type";
        this.openingHours = new HashSet<>();
        this.menuItems = new HashSet<>();
        this.employees = new HashSet<>();
        this.owner = new UserBuilder()
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();
        return this;
    }

    public RestaurantBuilder copy() {
        var b = new RestaurantBuilder().withDefaults();
        b.id = this.id;
        b.name = this.name;
        b.address = this.address;
        b.cuisineType = this.cuisineType;
        b.openingHours = new HashSet<>(this.openingHours);
        b.menuItems = new HashSet<>(this.menuItems);
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

    public RestaurantBuilder withMenuItems(Set<MenuItem> menuItems) {
        this.menuItems = (menuItems == null) ? new HashSet<>() : new HashSet<>(menuItems);
        return this;
    }

    public RestaurantBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public RestaurantBuilder withOwnerId(UUID ownerId) {
        this.owner = new UserBuilder()
                .withDefaults()
                .withId(ownerId)
                .withRole(UserRoles.RESTAURANT_OWNER)
                .build();
        return this;
    }

    public RestaurantBuilder withEmployee(User employee) {
        if (employee != null) this.employees.add(employee);
        return this;
    }

    public RestaurantBuilder withEmployees(Collection<? extends User> employees) {
        if (employees != null) this.employees.addAll(employees);
        return this;
    }

    public Restaurant build() {
        var restaurant = new Restaurant(id, name, address, cuisineType, owner);

        restaurant.addOpeningHours(openingHours == null ? Set.of() : openingHours);
        restaurant.addMenuItems(menuItems == null ? Set.of() : menuItems);
        restaurant.addEmployees(employees == null ? Set.of() : employees);

        return restaurant;
    }

    public RestaurantPublicOutput buildPublicOutput() {
        var addressOutput = address == null
                ? null
                : new AddressOutput(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getComplement()
        );

        Set<OpeningHoursOutput> openingHoursOutput = openingHours == null ? Set.of() :
                openingHours.stream()
                        .map(oh -> new OpeningHoursOutput(oh.getId(), oh.getDayOfWeek(), oh.getOpenHour(), oh.getCloseHour()))
                        .collect(Collectors.toSet());

        Set<MenuItemOutput> menuItemsOutput = menuItems == null ? Set.of() :
                menuItems.stream()
                        .map(m -> new MenuItemOutput(m.getId(), m.getName(), m.getDescription(), m.getPrice(), m.getRestaurantOnly(), m.getPhotoPath(), id))
                        .collect(Collectors.toSet());

        return new RestaurantPublicOutput(
                id,
                name,
                addressOutput,
                cuisineType,
                openingHoursOutput,
                menuItemsOutput
        );
    }
}
