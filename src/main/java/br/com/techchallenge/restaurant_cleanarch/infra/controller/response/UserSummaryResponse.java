package br.com.techchallenge.restaurant_cleanarch.infra.controller.response;

import java.util.UUID;

public record UserSummaryResponse(UUID uuid, String name) {}
