package br.com.techchallenge.restaurant_cleanarch.core.inbound;

import java.util.Set;
import java.util.UUID;

public record UpdateRestaurantInput (
    Long id,
    String name,
    AddressInput address,
    String cuisineType,
    Set<OpeningHoursInput> openingHours,
    Set<MenuItemInput> menu,
    UUID owner
){}
