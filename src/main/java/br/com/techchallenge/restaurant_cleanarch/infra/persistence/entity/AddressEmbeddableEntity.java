package br.com.techchallenge.restaurant_cleanarch.infra.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Embeddable
public class AddressEmbeddableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -7560528619565358826L;

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;
}
