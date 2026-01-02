package br.com.techchallenge.restaurant_cleanarch.core.outbound;

import java.util.Set;

public record UserTypeOutput(Long id, String name, Set<String> roles) {}
