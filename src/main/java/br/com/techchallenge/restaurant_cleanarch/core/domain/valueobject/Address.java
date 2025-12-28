package br.com.techchallenge.restaurant_cleanarch.core.domain.valueobject;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

@Getter
@Builder
public class Address {
    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;

    public void validateAddress() {
        if (street == null || street.trim().isEmpty()) {
            throw new BusinessException("Rua é obrigatória.");
        }
        // Adicionar outras validações para código postal, estado, etc.
    }
}
