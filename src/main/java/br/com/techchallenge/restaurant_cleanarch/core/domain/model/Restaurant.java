package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
public class Restaurant {
    private UUID id;
    private String name;
    private String address;
    private String cuisineType;
    private String openingHours;
    private User owner;

    public void validate() {
        if (owner == null || !owner.isRestaurantOwner()) {
            throw new BusinessException("O restaurante deve ter um dono válido.");
        }
        // Adicionar outras validações...
    }
}
