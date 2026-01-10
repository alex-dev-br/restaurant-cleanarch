package br.com.techchallenge.restaurant_cleanarch.core.inbound;

public record UpdateUserInput(
    String name,
    String email,
    AddressInput address,
    Long userTypeId
){}

