package br.com.techchallenge.restaurant_cleanarch.core.inbound;

import java.util.UUID;

public record UpdateUserInput(
    UUID id,
    String name,
    String email,
    AddressInput address,
    Long userTypeId
){}

