package br.com.techchallenge.restaurant_cleanarch.core.outbound;

import java.util.Set;
import java.util.UUID;

public record RestaurantOutput(
    Long id,
    String name,
    AddressOutput address,
    String cuisineType,
    Set<OpeningHoursOutput> openingHours,
    Set<MenuItemOutput> menuItems,
    UUID ownerId
) {}
