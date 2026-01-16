package br.com.techchallenge.restaurant_cleanarch.infra.controller.response;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email, AddressResponse address, UserTypeResponse userType) {}
