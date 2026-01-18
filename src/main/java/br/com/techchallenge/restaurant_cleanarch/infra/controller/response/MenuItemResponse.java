package br.com.techchallenge.restaurant_cleanarch.infra.controller.response;

import java.math.BigDecimal;

public record MenuItemResponse (
    Long id,
    String name,
    String description,
    BigDecimal price,
    Boolean restaurantOnly,
    String photoPath
){}
