package br.com.techchallenge.restaurant_cleanarch.core.usecase.restaurant;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.MenuItem;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Restaurant;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Address;
import br.com.techchallenge.restaurant_cleanarch.core.domain.model.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RestaurantRoles;
import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.techchallenge.restaurant_cleanarch.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.LoggedUserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.RestaurantGateway;
import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.AddressInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.MenuItemInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.OpeningHoursInput;
import br.com.techchallenge.restaurant_cleanarch.core.inbound.UpdateRestaurantInput;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateRestaurantUseCase {

    private final RestaurantGateway restaurantGateway;
    private final LoggedUserGateway loggedUserGateway;
    private final UserGateway userGateway;

    public UpdateRestaurantUseCase(RestaurantGateway restaurantGateway, UserGateway userGateway, LoggedUserGateway loggedUserGateway) {
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        Objects.requireNonNull(userGateway, "UserGateway cannot be null.");
        this.restaurantGateway = restaurantGateway;
        this.loggedUserGateway = loggedUserGateway;
        this.userGateway = userGateway;
    }


    public void execute(UpdateRestaurantInput input) {
        Objects.requireNonNull(input, "UpdateRestaurantInput cannot be null.");

        if (!loggedUserGateway.hasRole(RestaurantRoles.UPDATE_RESTAURANT))
            throw new OperationNotAllowedException("The current user does not have permission to update restaurants.");

        var restaurant = restaurantGateway.findById(input.id()).orElseThrow(() -> new BusinessException("Restaurant not found."));

        if (!restaurant.getName().equals(input.name()) && restaurantGateway.existsRestaurantWithName(input.name())) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        var owner = userGateway.findByUuid(input.owner()).orElseThrow(() -> new BusinessException("Owner not found."));

        if (!owner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        AddressInput addressInput = input.address();

        var updatedRestaurant = new Restaurant (
            input.id(),
            input.name(),
            addressInput != null ? buildAddress(addressInput) : null,
            input.cuisineType(),
            buildOpeningHours(input.openingHours()),
            buildMenu(input.menu(), restaurant),
            owner
        );

        restaurantGateway.save(updatedRestaurant);
    }

    private Set<MenuItem> buildMenu(Set<MenuItemInput> menuItemsInput, Restaurant restaurant) {
        if (menuItemsInput == null || menuItemsInput.isEmpty()) {
            return Set.of();
        }

        return menuItemsInput.stream()
                .map(m -> new MenuItem(
                        null,
                        m.name().trim(),
                        m.description() != null ? m.description().trim() : null,
                        m.price(),
                        m.restaurantOnly(),
                        m.photoPath().trim(),
                        restaurant  // ← Associação correta com restaurante persistido
                ))
                .collect(Collectors.toSet());
    }

    private Set<OpeningHours> buildOpeningHours(Set<OpeningHoursInput> openingHours) {
        if (openingHours == null || openingHours.isEmpty()) {
            return Set.of();
        }
        return openingHours.stream()
                .map(o -> new OpeningHours(null, o.dayOfDay(), o.openHour(), o.closeHour()))
                .collect(Collectors.toSet());
    }

    private Address buildAddress(AddressInput addressInput) {
        return addressInput == null
                ? null
                : new Address(
                addressInput.street(),
                addressInput.number(),
                addressInput.city(),
                addressInput.state(),
                addressInput.zipCode(),
                addressInput.complement()
        );
    }
}
