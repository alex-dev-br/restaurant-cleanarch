package br.com.techchallenge.restaurant_cleanarch.core.inbound;

public record CreateUserInput (
        String name,
        String email,
        String password,
        AddressInput address,
        Long userTypeId
) {}
