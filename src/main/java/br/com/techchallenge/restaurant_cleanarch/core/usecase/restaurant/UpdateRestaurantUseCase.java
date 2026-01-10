package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.User;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.valueobject.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;

import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateRestaurantUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public UpdateRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway, UserGateway userGateway) {
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        Objects.requireNonNull(userGateway, "UserGateway cannot be null");
        this.loggedUserGateway = loggedUserGateway;
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    public void execute(UpdateRestaurantInput input) {
        Objects.requireNonNull(input, "UpdateRestaurantInput cannot be null.");

        if (!loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT))
            throw new OperationNotAllowedException("The current user does not have permission to update restaurants.");

        var restaurant = restaurantGateway.findById(input.id()).orElseThrow(() -> new BusinessException("Restaurant not found."));
        User owner = userGateway.findById(input.owner()).orElseThrow(() -> new BusinessException("Owner not found."));

        if (!restaurant.getName().equals(owner.getName()) && restaurantGateway.existsRestaurantWithName(restaurant.getName())) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        if (!owner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        var address = buildAddress(input.address());
        var openingHours = input.openingHours() == null ? null : input.openingHours().stream()
                .map(oh -> new OpeningHours(oh.id(), oh.dayOfDay(), oh.openHour(), oh.closeHour()))
                        .collect(Collectors.toSet());
        var menu = input.menu() == null ? null : input.menu().stream()
                .map(i -> new MenuItem(i.id(), i.name(), i.description(), i.price(), i.restaurantOnly(), i.photoPath()))
                .collect(Collectors.toSet());

        var restaurantToUpdate = new Restaurant(input.id(), input.name(), address, input.cuisineType(), openingHours, menu, owner);

        restaurantGateway.save(restaurantToUpdate);
    }

    private Address buildAddress(AddressInput input) {
        return input == null ? null : new Address(
                input.street(),
                input.number(),
                input.city(),
                input.state(),
                input.zipCode(),
                input.complement()
        );
    }
}
