package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {
    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private Boolean restaurantOnly;

    @NotBlank
    @Size(min = 3, max = 255)
    private String photoPath;
}