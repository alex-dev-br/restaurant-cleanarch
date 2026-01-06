package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
public class Address {
    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;

    public Address(String street, String number, String city, String state, String zipCode, String complement) {
        if (street == null || street.trim().isEmpty()) {
            throw new BusinessException("Rua é obrigatória.");
        }
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.complement = complement;
    }
}
