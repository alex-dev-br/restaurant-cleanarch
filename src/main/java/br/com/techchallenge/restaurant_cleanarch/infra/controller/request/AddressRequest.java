package br.com.techchallenge.restaurant_cleanarch.infra.controller.request;

import lombok.Data;

@Data
public class AddressRequest {
    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;
}
