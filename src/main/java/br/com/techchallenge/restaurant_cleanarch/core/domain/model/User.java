package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
public class User {
    private UUID id;
    private String name;
    private String email;
    private Address address;
    private UserType userType;

    public void validate() {
        if(userType == null) {
            throw new BusinessException("Tipo de usuário é obrigatório.");
        }
        if(email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException("Email inválido.");
        }
        // Adicionar outras validações
    }

    public boolean isRestaurantOwner() {
        return "Dono de Restaurante".equalsIgnoreCase(this.userType.getName());
    }
}
