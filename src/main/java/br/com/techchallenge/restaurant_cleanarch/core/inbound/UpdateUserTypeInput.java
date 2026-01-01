package br.com.techchallenge.restaurant_cleanarch.core.inbound;

import java.util.Set;

public record UpdateUserTypeInput(Long id, String name, Set<String> roles) {}