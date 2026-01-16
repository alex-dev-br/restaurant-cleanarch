package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean restaurantOnly;
    private String photoPath;
}