package br.com.techchallenge.restaurant_cleanarch.core.domain.model.util;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.AddressOutput;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.AddressRequest;

public class AddressBuilder {

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;

    public AddressBuilder() {
        withDefaults();
    }

    public AddressBuilder withDefaults() {
        this.street = "Street";
        this.number = "123";
        this.city = "City";
        this.state = "State";
        this.zipCode = "12345678";
        this.complement = "Complement";
        return this;
    }

    public AddressBuilder copy() {
        var b = new AddressBuilder().withDefaults();
        b.street = this.street;
        b.number = this.number;
        b.city = this.city;
        b.state = this.state;
        b.zipCode = this.zipCode;
        b.complement = this.complement;
        return b;
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

    public AddressInput buildInput() {
        return new AddressInput(street, number, city, state, zipCode, complement);
    }

    public AddressOutput buildOutput() {
        return new AddressOutput(street, number, city, state, zipCode, complement);
    }

    public AddressRequest buildRequest() {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet(street);
        addressRequest.setNumber(number);
        addressRequest.setCity(city);
        addressRequest.setState(state);
        addressRequest.setZipCode(zipCode);
        addressRequest.setComplement(complement);
        return addressRequest;
    }
}
