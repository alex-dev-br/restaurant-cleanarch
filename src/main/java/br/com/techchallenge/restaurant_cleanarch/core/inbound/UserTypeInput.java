package br.com.techchallenge.restaurant_cleanarch.core.inbound;

import java.util.Set;

public record UserTypeInput(String name, Set<String> roles) {}