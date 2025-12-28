package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.Set;

@Getter
@Builder
public class Restaurant {
    private Long id;
    private String name;
    private Address address;
    private String cuisineType;
    private Set<OpeningHours> openingHours;
    private Set<MenuItem> menu;
    private User owner;

    public void validate() {
        if (owner == null || !owner.isRestaurantOwner()) {
            throw new BusinessException("O restaurante deve ter um dono válido.");
        }
        // Adicionar outras validações...
    }
}
