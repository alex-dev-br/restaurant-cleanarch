package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;

public class AddressBuilder {

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;

    public AddressBuilder() {
        this.street = "Rua Exemplo";
        this.number = "123";
        this.city = "São Paulo";
        this.state = "SP";
        this.zipCode = "01000-000";
        this.complement = "Apto 101";
    }

    public AddressBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public AddressBuilder withNumber(String number) {
        this.number = number;
        return this;
    }

    public AddressBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public AddressBuilder withState(String state) {
        this.state = state;
        return this;
    }

    public AddressBuilder withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public AddressBuilder withComplement(String complement) {
        this.complement = complement;
        return this;
    }

    public Address build() {
        return new Address(street, number, city, state, zipCode, complement);
    }
}
