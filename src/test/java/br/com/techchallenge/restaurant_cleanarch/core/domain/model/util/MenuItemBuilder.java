package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;

import java.math.BigDecimal;

public class MenuItemBuilder {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean restaurantOnly;
    private String photoPath;

    public MenuItemBuilder() {
        this.id = 1L;
        this.name = "Pizza Margherita";
        this.description = "Pizza clássica";
        this.price = new BigDecimal("30");
        this.restaurantOnly = false;
        this.photoPath = "/photos/pizza.jpg";
    }

    public MenuItemBuilder withoutId() {
        this.id = null;
        return this;
    }

    public MenuItemBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public MenuItemBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MenuItemBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public MenuItemBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuItemBuilder withRestaurantOnly(Boolean restaurantOnly) {
        this.restaurantOnly = restaurantOnly;
        return this;
    }

    public MenuItemBuilder withPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public MenuItem build() {
        return new MenuItem(id, name, description, price, restaurantOnly, photoPath);
    }
}
