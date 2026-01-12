package br.com.techchallenge.restaurant_cleanarch.core.outbound;

import java.util.UUID;

public record UserSummaryOutput(
    UUID id,
    String name
) {}
