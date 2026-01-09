package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantOutput;

import java.util.stream.Collectors;

public class RestaurantPresenter {

    private RestaurantPresenter() {}

    public static RestaurantOutput toOutput(Restaurant restaurant) {

        Long restaurantId = restaurant.getId();

        return new RestaurantOutput (
                restaurant.getId(),
                restaurant.getName(),
                AddressPresenter.toOutput(restaurant.getAddress()),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours().stream()
                        .map(OpeningHoursPresenter::toOutput)
                        .collect(Collectors.toSet()),
                restaurant.getMenu().stream()
                        .map(menuItem -> MenuItemPresenter.toOutput(menuItem, restaurantId))
                        .collect(Collectors.toSet()),
            restaurant.getOwner().getId()
        );
    }
}
