package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.CreateMenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.MenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateMenuItemInput;

import java.math.BigDecimal;

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

    /** Permite reutilizar o mesmo builder em vários testes sem “vazar” estado. */
    public MenuItemBuilder withDefaults() {
        this.id = null;
        this.name = "Pizza Margherita";
        this.description = "Pizza clássica";
        this.price = new BigDecimal("30");
        this.restaurantOnly = false;
        this.photoPath = "/photos/pizza.jpg";
        this.restaurantId = 1L;
        return this;
    }

    public MenuItemBuilder copy() {
        MenuItemBuilder b = new MenuItemBuilder();
        b.id = this.id;
        b.name = this.name;
        b.description = this.description;
        b.price = this.price;
        b.restaurantOnly = this.restaurantOnly;
        b.photoPath = this.photoPath;
        b.restaurantId = this.restaurantId;
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

    // novo
    public MenuItemBuilder withRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public MenuItem build() {
        return new MenuItem(
                id,
                name,
                description,
                price,
                restaurantOnly,
                photoPath
        );
    }

    public MenuItemInput buildInput() {
        return new MenuItemInput(
                name,
                description,
                price,
                restaurantOnly,
                photoPath
        );
    }

    public UpdateMenuItemInput buildUpdateInput() {
        return new UpdateMenuItemInput(
                id,
                name,
                description,
                price,
                restaurantOnly,
                photoPath
        );
    }


    public CreateMenuItemInput buildCreateInput() {
        return new CreateMenuItemInput(
                name,
                description,
                price,
                restaurantOnly,
                photoPath,
                restaurantId
        );
    }

    // opcional: útil quando você quer passar restaurantId direto sem mutar o builder
    public CreateMenuItemInput buildCreateInput(Long restaurantId) {
        return new CreateMenuItemInput(
                name,
                description,
                price,
                restaurantOnly,
                photoPath,
                restaurantId
        );
    }
}
