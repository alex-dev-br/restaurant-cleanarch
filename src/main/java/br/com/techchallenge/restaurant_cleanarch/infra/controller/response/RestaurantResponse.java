package br.com.techchallenge.restaurant_cleanarch.infra.controller.response;


import br.com.techchallenge.restaurant_cleanarch.infra.controller.request.OpeningHoursRequest;

import java.util.List;

public record RestaurantResponse (
        Long id,
        String name,
        String cuisineType,
        AddressResponse address,
        UserSummaryResponse owner,
        List<OpeningHoursRequest> openingHours,
        List<MenuItemResponse> menu,
        List<UserSummaryResponse> employees
){}
