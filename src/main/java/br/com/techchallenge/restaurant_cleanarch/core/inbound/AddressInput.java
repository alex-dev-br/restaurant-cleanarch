package br.com.techchallenge.restaurant_cleanarch.core.inbound;

public record AddressInput(String street, String number, String city, String state, String zipCode, String complement) {}
