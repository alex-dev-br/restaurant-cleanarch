package br.com.techchallenge.restaurant_cleanarch.core.outbound;

import java.util.Set;
import java.util.UUID;

public record CreateRestaurantOutput(
    Long id,
    String name,
    AddressOutput address,
    String cuisineType,
    Set<OpeningHoursOutput> openingHours,
    Set<MenuItemOutput> menu,
    UUID owner
) {}
