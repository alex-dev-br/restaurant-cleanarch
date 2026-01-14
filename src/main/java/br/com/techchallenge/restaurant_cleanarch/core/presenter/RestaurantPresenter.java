package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantManagementOutput;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.RestaurantPublicOutput;

import java.util.stream.Collectors;

public class RestaurantPresenter {

    private RestaurantPresenter() {}

    public static RestaurantPublicOutput toOutput(Restaurant restaurant) {

        Long restaurantId = restaurant.getId();

        return new RestaurantPublicOutput(
                restaurant.getId(),
                restaurant.getName(),
                AddressPresenter.toOutput(restaurant.getAddress()),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours().stream()
                        .map(OpeningHoursPresenter::toOutput)
                        .collect(Collectors.toSet()),
                restaurant.getMenu().stream()
                        .map(menuItem -> MenuItemPresenter.toOutput(menuItem, restaurantId))
                        .collect(Collectors.toSet())
        );
    }

    public static RestaurantManagementOutput toManagementOutput(Restaurant restaurant) {
        return new RestaurantManagementOutput(
                restaurant.getId(),
                restaurant.getName(),
                AddressPresenter.toOutput(restaurant.getAddress()),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours().stream().map(OpeningHoursPresenter::toOutput).collect(Collectors.toSet()),
                restaurant.getMenuItems().stream().map(m -> MenuItemPresenter.toOutput(m, restaurant.getId())).collect(Collectors.toSet()),
                restaurant.getEmployees().stream().map(UserPresenter::toSummaryOutput).collect(Collectors.toSet()),
                UserPresenter.toSummaryOutput(restaurant.getOwner())
        );
    }
}
