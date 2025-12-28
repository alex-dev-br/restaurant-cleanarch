package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
public class MenuItem {
    private UUID id;
    private String name;
    private String description;
    private double price;
    private boolean restaurantOnly;
    private String photoPath;
    private Restaurant restaurant;

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Nome do item do menu é obrigatório.");
        }
        if (price <= 0) {
            throw new BusinessException("Preço do item do menu deve ser maior que zero.");
        }
        if (restaurant == null) {
            throw new BusinessException("Item deve estar associado a um restaurante.");
        }
        // Adicionar outras validações conforme necessário
    }

}
