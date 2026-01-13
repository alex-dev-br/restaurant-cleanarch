package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.*;

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
                        .collect(Collectors.toUnmodifiableSet()),
                restaurant.getMenuItems().stream()
                        .map(menuItem -> MenuItemPresenter.toOutput(menuItem, restaurantId))
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    public static RestaurantManagementOutput toManagementOutput(Restaurant restaurant) {

        Long restaurantId = restaurant.getId();

        return new RestaurantManagementOutput(
                restaurant.getId(),
                restaurant.getName(),
                AddressPresenter.toOutput(restaurant.getAddress()),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours().stream()
                        .map(OpeningHoursPresenter::toOutput)
                        .collect(Collectors.toUnmodifiableSet()),
                restaurant.getMenuItems().stream()
                        .map(menuItem -> MenuItemPresenter.toOutput(menuItem, restaurantId))
                        .collect(Collectors.toUnmodifiableSet()),
                restaurant.getEmployees().stream()
                        .map(u -> new UserSummaryOutput(u.getId(), u.getName()))
                        .collect(Collectors.toUnmodifiableSet()),
            restaurant.getOwner().getId()
        );
    }
}
