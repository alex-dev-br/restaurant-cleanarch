package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.MenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.MenuItemOutput;

import java.math.BigDecimal;
import java.util.Objects;

public class MenuItemBuilder {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean restaurantOnly;
    private String photoPath;

    private Long restaurantId;

    public MenuItemBuilder() {
        withDefaults();
    }

    public MenuItemBuilder withDefaults() {
        this.id = 1L;
        this.name = "Dish Name";
        this.description = "Dish Description";
        this.price = BigDecimal.valueOf(10.0);
        this.restaurantOnly = false;
        this.photoPath = "photo.jpg";
        this.restaurantId = 1L; //
        return this;
    }

    public MenuItemBuilder copy() {
        var b = new MenuItemBuilder().withDefaults();
        b.id = this.id;
        b.name = this.name;
        b.description = this.description;
        b.price = this.price;
        b.restaurantOnly = this.restaurantOnly;
        b.photoPath = this.photoPath;
        b.restaurantId = this.restaurantId; //
        return b;
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

    public MenuItemBuilder withRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public MenuItem build() {
        return new MenuItem(id, name, description, price, restaurantOnly, photoPath);
    }

    public MenuItemInput buildInput() {
        return new MenuItemInput(name, description, price, restaurantOnly, photoPath);
    }

    public CreateMenuItemInput buildCreateInput() {
        return buildCreateInput(Objects.requireNonNull(restaurantId, "restaurantId cannot be null"));
    }

    public CreateMenuItemInput buildCreateInput(Long restaurantId) {
        return new CreateMenuItemInput(name, description, price, restaurantOnly, photoPath, restaurantId);
    }

    public UpdateMenuItemInput buildUpdateInput() {
        return new UpdateMenuItemInput(id, name, description, price, restaurantOnly, photoPath);
    }

    public MenuItemOutput buildOutput(Long restaurantId) {
        return new MenuItemOutput(id, name, description, price, restaurantOnly, photoPath, restaurantId);
    }
}
