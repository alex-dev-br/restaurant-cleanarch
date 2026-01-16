package br.com.techchallenge.restaurant_cleanarch.infra.controller.response;

import java.util.List;

public record UserTypeResponse(Long id, String name, List<String> roles) {}
