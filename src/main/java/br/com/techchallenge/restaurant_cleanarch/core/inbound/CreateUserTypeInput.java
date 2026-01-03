package br.com.techchallenge.restaurant_cleanarch.core.inbound;

import java.util.Set;

public record CreateUserTypeInput(String name, Set<String> roles) {}