package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.MenuItemInput;

import java.math.BigDecimal;

public class MenuItemBuilder {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean restaurantOnly;
    private String photoPath;
    private Restaurant restaurant;  // ← campo obrigatório

    public MenuItemBuilder() {
        this.id = 1L;
        this.name = "Pizza Margherita";
        this.description = "Pizza clássica";
        this.price = new BigDecimal("30");
        this.restaurantOnly = false;
        this.photoPath = "/photos/pizza.jpg";
        // Valor padrão: usa o RestaurantBuilder existente (assumindo que ele tem valores padrão válidos)
        this.restaurant = new RestaurantBuilder().build();
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

    /**
     * Permite sobrescrever o restaurante padrão nos testes
     */
    public MenuItemBuilder withRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        return this;
    }

    public MenuItem build() {
        return new MenuItem(
                id,
                name,
                description,
                price,
                restaurantOnly,
                photoPath,
                restaurant  // ← Agora passamos o restaurante
        );
    }

    public MenuItemInput buildInput() {
        return new MenuItemInput(name, description, price, restaurantOnly, photoPath);
    }
}