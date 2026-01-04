package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AddressEmbeddableEntity {
    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;
}
