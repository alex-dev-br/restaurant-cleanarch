package br.com.techchallenge.restaurant_cleanarch.core.outbound;

public record AddressOutput(String street, String number, String city, String state, String zipCode, String complement) {}
