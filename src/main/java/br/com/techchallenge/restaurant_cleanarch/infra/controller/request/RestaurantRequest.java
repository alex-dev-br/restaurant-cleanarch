package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

@Data
public class RestaurantRequest {
    @NotEmpty
    @Size(min = 2, max = 100)
    private String name;
    @NotEmpty
    @Length(min = 4, max = 100)
    private String cuisineType;
    @NotNull
    private AddressRequest address;
    @NotNull
    private UUID ownerId;

    @NotNull
    @Size(min = 1, max = 7)
    private List<OpeningHoursRequest> openingHours;

    private List<MenuItemRequest> menu;

    private List<UUID> employees;
}