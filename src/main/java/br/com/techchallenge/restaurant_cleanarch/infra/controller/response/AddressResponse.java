package br.com.techchallenge.restaurant_cleanarch.infra.controller.response;

public record AddressResponse (
        String street,
        String number,
        String city,
        String state,
        String zipCode,
        String complement
) {}
