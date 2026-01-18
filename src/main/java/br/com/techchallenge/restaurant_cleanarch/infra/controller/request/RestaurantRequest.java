package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RestaurantRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 4, max = 100)
    private String cuisineType;

    @Valid
    @NotNull
    private AddressRequest address;
    @NotNull
    private UUID ownerId;

    @Valid
    @NotNull
    @Size(min = 1, max = 7)
    private List<OpeningHoursRequest> openingHours;

    @Valid
    private List<MenuItemRequest> menu;

    private List<UUID> employees;
}